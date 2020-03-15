package chat.utils;

import chat.client.Client;
import chat.server.Server;
import javafx.scene.control.Menu;
import java.util.Locale;
import java.util.ResourceBundle;

public class Language {
    private static ResourceBundle bundle;

    public static void setRussian(Object controller) {
        bundle = ResourceBundle.getBundle("files.lang.Chat", new Locale("ru"));
        update(controller);
    }

    public static void setEnglish(Object controller) {
        bundle = ResourceBundle.getBundle("files.lang.Chat", new Locale("en"));
        update(controller);
    }

    public static void setDeutsch(Object controller) {
        bundle = ResourceBundle.getBundle("files.lang.Chat", new Locale("de"));
        update(controller);
    }

    public static void setFrench(Object controller) {
        bundle = ResourceBundle.getBundle("files.lang.Chat", new Locale("fr"));
        update(controller);
    }

    public static void setSpain(Object controller) {
        bundle = ResourceBundle.getBundle("files.lang.Chat", new Locale("es"));
        update(controller);
    }

    public static void setItalian(Object controller) {
        bundle = ResourceBundle.getBundle("files.lang.Chat", new Locale("it"));
        update(controller);
    }

    private static void update(Object controller) {
        if (controller instanceof Client) {
            Client c = (Client) controller;
            changeLanguageClient(c);
        } else {
            Server c = (Server) controller;
            changeLanguageServer(c);
        }

    }

    private static void changeLanguageClient(Client client) {
        client.menuBar.getMenus().forEach(menu -> {
            menu.setText(bundle.getString(menu.getId()));
            menu.getItems().forEach(menuItem -> {
                menuItem.setText(bundle.getString(menuItem.getId()));
                if (menuItem instanceof Menu) {
                    Menu item = (Menu) menuItem;
                    item.getItems().forEach(elem -> {
                        elem.setText(bundle.getString(elem.getId()));
                        if (elem instanceof Menu) {
                            Menu menuElem = (Menu) elem;
                            menuElem.getItems().forEach(childMenuElem -> {
                                if (!childMenuElem.getParentMenu().getId().equals("menuFamily")
                                        && !childMenuElem.getParentMenu().getId().equals("menuSize"))
                                    childMenuElem.setText(bundle.getString(childMenuElem.getId()));
                            });
                        }
                    });
                }
            });
        });

        client.stage.setTitle(bundle.getString("clientTitle"));
        client.startWindow.setTitle(bundle.getString("chatTitle"));
        client.connectBtn.setText(bundle.getString("connectBtn"));
        client.disconnectBtn.setText(bundle.getString("disconnectBtn"));
        client.sendBtn.setText(bundle.getString("sendBtn"));
        client.labelPort.setText(bundle.getString("labelPort"));
        client.labelName.setText(bundle.getString("labelName"));
        client.inMessage.setPromptText(bundle.getString("inMessage"));
        client.outMessage.setPromptText(bundle.getString("outMessage"));
        client.onlineStr = bundle.getString("paneListView");
        client.paneListView.setText(client.onlineStr + client.clientListName.size());
    }

    private static void changeLanguageServer(Server server) {
        server.menuBar.getMenus().forEach(menu -> {
            menu.setText(bundle.getString(menu.getId()));
            menu.getItems().forEach(menuItem -> {
                menuItem.setText(bundle.getString(menuItem.getId()));
                if (menuItem instanceof Menu) {
                    Menu item = (Menu) menuItem;
                    item.getItems().forEach(elem -> {
                        elem.setText(bundle.getString(elem.getId()));
                        if (elem instanceof Menu) {
                            Menu menuElem = (Menu) elem;
                            menuElem.getItems().forEach(childMenuElem -> {
                                if (!childMenuElem.getParentMenu().getId().equals("menuFamily")
                                        && !childMenuElem.getParentMenu().getId().equals("menuSize"))
                                    childMenuElem.setText(bundle.getString(childMenuElem.getId()));
                            });
                        }
                    });
                }
            });
        });

        server.stage.setTitle(bundle.getString("serverTitle"));
        server.startWindow.setTitle(bundle.getString("chatTitle"));
        server.connectBtn.setText(bundle.getString("connectBtn"));
        server.disconnectBtn.setText(bundle.getString("disconnectBtn"));
        server.sendBtn.setText(bundle.getString("sendBtn"));
        server.labelPort.setText(bundle.getString("labelPort"));
        server.labelName.setText(bundle.getString("labelName"));
        server.inMessage.setPromptText(bundle.getString("inMessage"));
        server.outMessage.setPromptText(bundle.getString("outMessage"));
        server.onlineStr = bundle.getString("paneListView");
        server.paneListView.setText(server.onlineStr + server.clientListName.size());
    }
}