package dev.comfast.integration;
import dev.comfast.cf.CfLocator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import static dev.comfast.cf.CfApi.$;
import static dev.comfast.cf.CfApi.open;
import static org.assertj.core.api.Assertions.assertThat;

@EnabledIfSystemProperty(named = "allTests", matches = "true")
class UtilsTest {
    @Test void clearBrowserDataTest() {
        open("chrome://settings/clearBrowserData");

        CfLocator rootInShadowDom = $("body settings-ui >> #main >> settings-basic-page >> " +
                                      "#basicPage settings-privacy-page >> settings-clear-browsing-data-dialog");
        CfLocator clearDataBtn = rootInShadowDom.$("#clearBrowsingDataConfirm");
        CfLocator loading = rootInShadowDom.$(".paper-spinner-lite");

        clearDataBtn.click();
//        loading.should(disappear);
        assertThat(loading.isDisplayed()).isFalse();
    }
}
