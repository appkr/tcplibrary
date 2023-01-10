package com.vroong.tcp.visitor.example;

import static com.vroong.tcp.config.GlobalConstants.DEFAULT_CHARSET;

import com.vroong.tcp.Item;
import com.vroong.tcp.Packet;
import com.vroong.tcp.TcpMessage;
import com.vroong.tcp.TcpMessageTemplateFactory;
import com.vroong.tcp.visitor.Parser;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Setter;

public class FixedByteParser implements Parser {

  @Setter
  private Charset charset = DEFAULT_CHARSET;

  private final TcpMessageTemplateFactory templateFactory;

  public FixedByteParser(TcpMessageTemplateFactory templateFactory) {
    this.templateFactory = templateFactory;
  }

  @Override
  public void parse(Packet parseable) {
    final byte[] src = parseable.getTcpMessage();
    final List<TcpMessage> components = templateFactory.create(src);
    parseable.setMessageComponents(components);

    doParse(src, components, new AtomicInteger(0));
  }

  private void doParse(byte[] src, List<TcpMessage> components, AtomicInteger subPacketIndex) {
    final AtomicInteger srcPos = new AtomicInteger(0);
    components.stream()
        .forEach(component -> {
          final int length = getLength(component);
          final byte[] fragment = new byte[length];
          System.arraycopy(src, srcPos.getAndAdd(length), fragment, 0, length);

          if (component instanceof Item) {
            final String value = new String(fragment, charset).trim();
            component.setValue(value);
          } else {
            final String subPacketName = String.valueOf(subPacketIndex.getAndIncrement());
            component.setName(subPacketName);
            ((Packet) component).setTcpMessage(fragment);
            doParse(fragment, ((Packet) component).getMessageComponents(), subPacketIndex);
          }
        });
  }

  private int getLength(TcpMessage component) {
    if (component instanceof Item) {
      return component.getPointer();
    }

    return ((Packet) component).getMessageComponents().stream()
        .map(this::getLength)
        .reduce(Integer::sum)
        .orElse(0);
  }
}
