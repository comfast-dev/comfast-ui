package dev.comfast.cf.common.utils;
import dev.comfast.util.TempFile;
import lombok.SneakyThrows;
import org.intellij.lang.annotations.Language;

import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static dev.comfast.cf.CfApi.executeJs;
import static dev.comfast.cf.CfApi.open;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Utility class to write browser html / css / js
 */
@SuppressWarnings({"UnusedReturnValue", "unused", "JSUnusedLocalSymbols"})
public class BrowserContent {
    /**
     * Copy given resource file to temp folder and open it in browser.
     * @param resourcePath path to resource file, where root is "src/test/resources"
     */
    @SneakyThrows
    public void openResourceFile(String resourcePath) {
        URL resource = getClass().getClassLoader().getResource(resourcePath);
        if(resource == null) throw new FileNotFoundException("Resource not found: " + resourcePath);

        var tempFile = new TempFile("comfast-unit-tests/" + resourcePath);
        tempFile.write(Files.readString(Path.of(resource.toURI()), UTF_8));

        open(tempFile.file.toUri().toURL().toString());
    }

    /**
     * Set style tag to head section
     */
    public BrowserContent setStyle(@Language("CSS") String styleContent) {
        executeJs("document.querySelector('html>head').innerHTML = arguments[0]",
            "<style>" + styleContent + "</style>");
        return this;
    }

    /**
     * Set body tag content
     */
    public BrowserContent setBody(@Language("HTML") String bodyHtml) {
        executeJs("document.querySelector('html>body').innerHTML = arguments[0]", bodyHtml);
        return this;
    }

    /**
     * Add $ and $$ JS methods to current page for convenience
     */
    public BrowserContent addJsTools() {
        setScriptTag(
            "const $$ = (css, parent = document) => Array.from(parent.querySelectorAll(css));\n" +
            "const $ =  (css, parent = document) => parent.querySelector(css);\n"
            );
        return this;
    }

    /**
     * Adds script tag to head section
     */
    public BrowserContent setScriptTag(@Language("JavaScript") String scriptContent) {
        String escapedScriptContent = scriptContent.replace("`", "\\`");
        @Language("JavaScript") final String ADD_SCRIPT =
            "const s = document.createElement('script');" +
            "s.innerHTML = `" + escapedScriptContent + "`;" +
            " document.querySelector('head').append(s);";
        executeJs(ADD_SCRIPT);
        return this;
    }

    /**
     * Clear all html content
     */
    public void clearAll() {
        executeJs("document.querySelector('html').innerHTML = ''");
    }
}