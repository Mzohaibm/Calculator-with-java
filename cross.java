import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StripeWebhookController {

    private final String stripeSecretKey = "your_stripe_secret_key";
    private final String webhookSigningSecret = "your_webhook_signing_secret";

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSigningSecret);

            // Handle the event
            switch (event.getType()) {
                case "payment_intent.succeeded":
                    PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject();
                    // Handle successful payment
                    break;
                // event handlers as needed

                default:
                    // Unexpected event type
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unexpected event type");
            }

            // a response to acknowledge receipt of the event
            return ResponseEntity.ok().body("{\"received\": true}");

        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook signature verification failed");
        }
    }
}
