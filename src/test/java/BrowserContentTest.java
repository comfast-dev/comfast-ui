import dev.comfast.cf.CfApi;
import dev.comfast.cf.common.utils.BrowserContent;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class BrowserContentTest {
    @Disabled
    @Test void trustedHtmlTest() {
        try {
            Assertions.assertThatCode(() -> {
                CfApi.open("brave://settings/privacy");
                new BrowserContent().setBody("<ul><li>xxx</li></ul>");
            }).doesNotThrowAnyException();
        } finally {
            CfApi.open("chrome://newTab");
        }
    }
}
