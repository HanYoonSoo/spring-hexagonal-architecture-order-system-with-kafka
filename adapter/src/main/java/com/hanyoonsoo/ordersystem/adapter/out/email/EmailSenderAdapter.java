package com.hanyoonsoo.ordersystem.adapter.out.email;

import com.hanyoonsoo.ordersystem.application.email.port.out.EmailSender;
import com.hanyoonsoo.ordersystem.application.email.event.EmailSendRequestedEvent;
import com.hanyoonsoo.ordersystem.core.domain.order.entity.OrderStatus;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class EmailSenderAdapter implements EmailSender {

    private final JavaMailSender javaMailSender;

    @Override
    public void sendOrderResultEmail(String email, EmailSendRequestedEvent event) {
        if (!StringUtils.hasText((email))) return;

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setTo(email);
            helper.setSubject(buildSubject(event));
            helper.setText(buildHtmlContent(event), true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("주문 결과 이메일 전송에 실패했습니다.", e);
        }
    }

    private String buildSubject(EmailSendRequestedEvent event) {
        return switch (event.orderStatus()) {
            case CONFIRMED -> "주문이 승인되었습니다.";
            case REJECTED_OUT_OF_STOCK -> "주문이 거절되었습니다.";
            default -> "주문 처리 결과 안내";
        };
    }

    private String buildHtmlContent(EmailSendRequestedEvent event) {
        String title = event.orderStatus() == OrderStatus.CONFIRMED ? "주문이 승인되었습니다." : "주문이 거절되었습니다.";
        String description = event.orderStatus() == OrderStatus.CONFIRMED
                ? "결제가 완료되면 배송 준비가 시작됩니다."
                : "재고가 부족하여 주문을 처리하지 못했습니다.";

        return """
                <html>
                <body style="margin:0;padding:24px;background:#f5f7fb;font-family:Arial,sans-serif;color:#1f2937;">
                  <div style="max-width:560px;margin:0 auto;background:#ffffff;border:1px solid #e5e7eb;border-radius:16px;padding:32px;">
                    <h1 style="margin:0 0 16px;font-size:24px;">%s</h1>
                    <p style="margin:0 0 24px;font-size:15px;line-height:1.6;">%s</p>
                    <div style="padding:16px;background:#f9fafb;border-radius:12px;">
                      <p style="margin:0 0 8px;font-size:14px;"><strong>주문 번호</strong>: %s</p>
                      <p style="margin:0 0 8px;font-size:14px;"><strong>상품 ID</strong>: %s</p>
                      <p style="margin:0;font-size:14px;"><strong>수량</strong>: %s</p>
                    </div>
                  </div>
                </body>
                </html>
                """.formatted(title, description, event.orderId(), event.productId(), event.quantity());
    }
}
