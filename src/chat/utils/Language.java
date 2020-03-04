package chat.utils;

import chat.client.Client;

import java.util.Locale;
import java.util.ResourceBundle;

public class Language {
    private static ResourceBundle bundle;

    public static void setRussian(Client controller) {
        bundle = ResourceBundle.getBundle("files.lang.Chat", new Locale("ru"));
        update(controller);
    }

    public static void setEnglish(Client controller) {
        bundle = ResourceBundle.getBundle("files.lang.Chat", new Locale("en"));
        update(controller);
    }

    public static void setDeutsch(Client controller) {
        bundle = ResourceBundle.getBundle("files.lang.Chat", new Locale("de"));
        update(controller);
    }

    public static void setFrench(Client controller) {
        bundle = ResourceBundle.getBundle("files.lang.Chat", new Locale("fr"));
        update(controller);
    }

    public static void setSpain(Client controller) {
        bundle = ResourceBundle.getBundle("files.lang.Chat", new Locale("es"));
        update(controller);
    }

    public static void setItalian(Client controller) {
        bundle = ResourceBundle.getBundle("files.lang.Chat", new Locale("it"));
        update(controller);
    }

    private static void update(Client controller) {
        controller.connectBtn.setText(bundle.getString("connectBtn"));
        controller.disconnectBtn.setText(bundle.getString("disconnectBtn"));
        controller.sendBtn.setText(bundle.getString("sendBtn"));
        controller.labelPort.setText(bundle.getString("labelPort"));
        controller.labelName.setText(bundle.getString("labelName"));
        controller.inMessage.setPromptText(bundle.getString("inMessage"));
        controller.outMessage.setPromptText(bundle.getString("outMessage"));
        controller.paneListView.setText(bundle.getString("paneListView") + controller.clientListName.size());

        controller.menuFile.setText(bundle.getString("menuFile"));
        controller.menuOpen.setText(bundle.getString("menuOpen"));
        controller.menuSave.setText(bundle.getString("menuSave"));
        controller.menuExit.setText(bundle.getString("menuExit"));

        controller.menuSettings.setText(bundle.getString("menuSettings"));
        controller.menuTheme.setText(bundle.getString("menuTheme"));
        controller.menuDefault.setText(bundle.getString("menuDefault"));
        controller.menuDark.setText(bundle.getString("menuDark"));
        controller.menuLang.setText(bundle.getString("menuLang"));
        controller.menuRu.setText(bundle.getString("menuRu"));
        controller.menuEn.setText(bundle.getString("menuEn"));
        controller.menuDe.setText(bundle.getString("menuDe"));
        controller.menuFr.setText(bundle.getString("menuFr"));
        controller.menuEs.setText(bundle.getString("menuEs"));
        controller.menuIt.setText(bundle.getString("menuIt"));
        controller.menuFont.setText(bundle.getString("menuFont"));
        controller.menuFamily.setText(bundle.getString("menuFamily"));
        controller.menuStyle.setText(bundle.getString("menuStyle"));
        controller.menuSize.setText(bundle.getString("menuSize"));
        controller.menuColor.setText(bundle.getString("menuColor"));

        controller.menuReference.setText(bundle.getString("menuReference"));
        controller.menuGitHub.setText(bundle.getString("menuGitHub"));
        controller.menuAbout.setText(bundle.getString("menuAbout"));
        controller.menuAutor.setText(bundle.getString("menuAutor"));
    }
}