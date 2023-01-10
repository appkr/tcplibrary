package com.vroong.tcp.visitor.example;

import static com.vroong.tcp.config.GlobalConstants.DEFAULT_CHARSET;

import com.vroong.tcp.Item;
import com.vroong.tcp.Packet;
import com.vroong.tcp.TcpMessage;
import com.vroong.tcp.visitor.Formatter;
import java.nio.charset.Charset;
import java.util.List;
import lombok.Setter;

public class FixedByteFormatter implements Formatter {

  @Setter
  private Charset charset = DEFAULT_CHARSET;

  @Setter
  private String leftPadding = "0";


  @Override
  public void format(Packet formattable) {
    final String formatted = doFormat(formattable.getMessageComponents());
    formattable.setTcpMessage(formatted.getBytes(charset));
  }

  private String doFormat(List<TcpMessage> components) {
    final StringBuilder formatted = new StringBuilder();
    components
        .forEach(component -> {
          String fragment = "";
          if (component instanceof Item) {
            fragment = formatFor(component);
          } else {
            fragment = doFormat(((Packet) component).getMessageComponents());
          }

          formatted.append(fragment);
        });

    return formatted.toString();
  }

  private String formatFor(TcpMessage item) {
    final int expectedLength = item.getPointer();
    final String value = item.getValue();
    final int lpadCount = expectedLength - value.getBytes(charset).length;
    final StringBuilder sb = new StringBuilder();
    while (sb.length() < lpadCount) {
      sb.append(leftPadding);
    }

    return sb.append(value).toString();
  }
}
