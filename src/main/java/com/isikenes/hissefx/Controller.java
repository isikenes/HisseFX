package com.isikenes.hissefx;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import javafx.util.Duration;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Controller implements Initializable {

    @FXML
    TextField hisseText;
    @FXML
    Button plusButton;

    @FXML
    ListView<String> hisseList;
    @FXML
    ListView<String> fiyatList;
    @FXML
    ListView<String> degisimList;

    ArrayList<String> hisseler = new ArrayList<>();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        hisseText.setFocusTraversable(false);
        hisseList.setMouseTransparent(true);
        hisseList.setFocusTraversable(false);
        fiyatList.setMouseTransparent(true);
        fiyatList.setFocusTraversable(false);
        degisimList.setMouseTransparent(true);
        degisimList.setFocusTraversable(false);

        hisseler = load();
        UpdateTable();

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), actionEvent -> UpdateTable()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        plusButton.setDefaultButton(true);
    }

    private void UpdateTable() {

        String url = "https://www.isyatirim.com.tr/tr-tr/analiz/hisse/Sayfalar/default.aspx";
        Elements rows;
        try {
            Document doc = Jsoup.connect(url).get();

            Element table = doc.select("table.dataTable").first();
            rows = table.select("tr");

            if (!rows.isEmpty()) {
                hisseList.getItems().clear();
                fiyatList.getItems().clear();
                degisimList.getItems().clear();

                for (String s : hisseler) {
                    String fiyat = "";
                    String degisim = "";
                    for (Element row : rows) {
                        if (row.select("td:first-child").text().equals(s)) {
                            Elements cells = row.select("td");
                            fiyat = cells.get(1).text();
                            degisim = cells.get(2).select("span.value").first().text();
                            break;
                        }
                    }
                    hisseList.getItems().add(s);
                    fiyatList.getItems().add(fiyat + " TL");
                    degisimList.getItems().add(degisim + "%");


                    degisimList.setCellFactory(new Callback<>() {
                        @Override
                        public ListCell<String> call(ListView<String> stringListView) {
                            return new ListCell<>() {
                                @Override
                                protected void updateItem(String s, boolean b) {
                                    super.updateItem(s, b);
                                    if (b || s == null) {
                                        setText(null);
                                        setStyle("-fx-background-color: #333333");
                                    } else if (s.charAt(0) == '-') {
                                        setText(s);
                                        setStyle("-fx-background-color: #c21414");
                                    } else {
                                        setText(s);
                                        setStyle("-fx-background-color: #249e10");
                                    }
                                }
                            };
                        }
                    });
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void onPlusClick() {

        String input = hisseText.getText().toUpperCase();
        if (input.isEmpty()) {
            return;
        }
        String url = "https://www.isyatirim.com.tr/tr-tr/analiz/hisse/Sayfalar/default.aspx";

        try {
            Document doc = Jsoup.connect(url).get();

            Element table = doc.select("table.dataTable").first();
            Elements rows = table.select("tr");
            boolean isFound = false;
            for (Element row : rows) {
                if (row.select("td:first-child").text().equals(input)) {
                    isFound = true;
                    break;
                }
            }

            if (!isFound) {
                hisseText.setPromptText("404! Hisse bulunamadı!");
            } else {
                if (!hisseler.contains(input)) {
                    hisseler.add(input);
                    save();
                    UpdateTable();
                    hisseText.setPromptText("örn: ZRGYO");
                }

            }
            hisseText.clear();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> load() {
        ArrayList<String> list = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new File("Hisseler.txt"));
            while (scanner.hasNextLine()) {
                list.add(scanner.nextLine());
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return list;
    }

    private void save() {
        Path path = Paths.get("Hisseler.txt");
        try {
            Files.write(path, hisseler, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}