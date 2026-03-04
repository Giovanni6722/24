package org.gp._4;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class HelloApplication extends Application
{
    private final ImageView card1 = new ImageView();
    private final ImageView card2 = new ImageView();
    private final ImageView card3 = new ImageView();
    private final ImageView card4 = new ImageView();

    private final Label cardValuesLabel = new Label("Card Values: [?, ?, ?, ?]");
    private final Label statusLabel = new Label("Waiting for input...");
    private final TextField expressionField = new TextField();

    @Override
    public void start(Stage stage)
    {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #f6f7fb; -fx-font-size: 14px;");

        Pane gamePage = buildGamePage(root);
        Pane rulesPage = buildRulesPage(root);

        rulesPage.setVisible(false);
        rulesPage.setManaged(false);

        root.getChildren().addAll(gamePage, rulesPage);

        Scene scene = new Scene(root, 900, 560);
        stage.setTitle("Card Game - 24");
        stage.setScene(scene);
        stage.show();
    }

    private void showPage(StackPane root, int indexToShow)
    {
        for (int i = 0; i < root.getChildren().size(); i++)
        {
            boolean show = (i == indexToShow);
            root.getChildren().get(i).setVisible(show);
            root.getChildren().get(i).setManaged(show);
        }
    }

    private Pane buildGamePage(StackPane root)
    {
        Label title = new Label("Card Game - 24");
        title.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 26));

        Label subtitle = new Label("Use all 4 card values exactly once to make 24 ( +  -  *  /  and parentheses )");
        subtitle.setOpacity(0.75);

        VBox header = new VBox(6, title, subtitle);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(16, 16, 10, 16));

        configureCardView(card1);
        configureCardView(card2);
        configureCardView(card3);
        configureCardView(card4);

        HBox cardsRow = new HBox(16, cardSlot(card1), cardSlot(card2), cardSlot(card3), cardSlot(card4));
        cardsRow.setAlignment(Pos.CENTER);

        cardValuesLabel.setOpacity(0.75);

        VBox centerArea = new VBox(14, cardsRow, cardValuesLabel);
        centerArea.setAlignment(Pos.CENTER);
        centerArea.setPadding(new Insets(18));

        Label enterLabel = new Label("Enter expression:");
        enterLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        expressionField.setPromptText("Example: (8-4)*(7-1)");
        HBox.setHgrow(expressionField, Priority.ALWAYS);

        Button verifyBtn = new Button("Verify");
        verifyBtn.setDefaultButton(true);

        Button refreshBtn = new Button("Refresh");

        Button rulesBtn = new Button("Rules");
        rulesBtn.setOnAction(e -> showPage(root, 1));

        HBox controlsRow = new HBox(10, expressionField, verifyBtn, refreshBtn, rulesBtn);
        controlsRow.setAlignment(Pos.CENTER_LEFT);

        Separator sep = new Separator();

        Label statusTitle = new Label("Status:");
        statusTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        statusLabel.setOpacity(0.85);

        HBox statusRow = new HBox(10, statusTitle, statusLabel);
        statusRow.setAlignment(Pos.CENTER_LEFT);

        VBox panel = new VBox(10, enterLabel, controlsRow, sep, statusRow);
        panel.setPadding(new Insets(16));
        panel.setStyle
                ("""
                -fx-background-color: white;
                -fx-border-color: #e6e8ef;
                -fx-border-radius: 14;
                -fx-background-radius: 14;
                """);

        VBox bottomArea = new VBox(panel);
        bottomArea.setPadding(new Insets(0, 16, 16, 16));

        verifyBtn.setOnAction(e -> statusLabel.setText("Verify clicked (logic coming next)."));
        refreshBtn.setOnAction(e -> statusLabel.setText("Refresh clicked (logic coming next)."));

        BorderPane layout = new BorderPane();
        layout.setTop(header);
        layout.setCenter(centerArea);
        layout.setBottom(bottomArea);
        layout.setStyle("-fx-background-color: transparent;");

        return layout;
    }

    private Pane buildRulesPage(StackPane root)
    {
        Label title = new Label("Rules (Card 24)");
        title.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 22));

        Label rulesText = new Label
                ("""
                Goal:
                Use the four card values to create an arithmetic expression that evaluates to 24.

                Card Values:
                • Ace = 1
                • 2–10 = face value
                • Jack = 11
                • Queen = 12
                • King = 13

                Rules:
                • You must use ALL four values exactly once.
                • Allowed operations: +  -  *  /
                • Parentheses are allowed.
                • Your expression must evaluate to 24.

                Buttons:
                • Verify: checks if your expression uses the correct values and equals 24.
                • Refresh: deals a new set of four cards.
                """);
        rulesText.setWrapText(true);

        ScrollPane scroll = new ScrollPane(rulesText);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:transparent;");

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> showPage(root, 0));

        VBox card = new VBox(12, title, scroll, backBtn);
        card.setPadding(new Insets(16));
        card.setMaxWidth(700);
        card.setStyle
                ("""
                -fx-background-color: white;
                -fx-border-color: #e6e8ef;
                -fx-border-radius: 14;
                -fx-background-radius: 14;
                """);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        BorderPane layout = new BorderPane();
        layout.setCenter(card);
        BorderPane.setMargin(card, new Insets(16));
        layout.setStyle("-fx-background-color: transparent;");

        return layout;
    }

    private static void configureCardView(ImageView iv)
    {
        iv.setFitWidth(120);
        iv.setFitHeight(170);
        iv.setPreserveRatio(true);
        // iv.setImage(...) later
    }

    private static StackPane cardSlot(ImageView iv)
    {
        StackPane slot = new StackPane(iv);
        slot.setMinSize(132, 182);
        slot.setPadding(new Insets(8));
        slot.setStyle
                ("""
                -fx-background-color: white;
                -fx-border-color: #e6e8ef;
                -fx-border-radius: 16;
                -fx-background-radius: 16;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.10), 10, 0.25, 0, 3);
                """);
        return slot;
    }

    public static void main(String[] args) {launch(args);}
}