package org.gp._4;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.*;

public class HelloApplication extends Application
{
    private static final String CARD_DIR = "/cards/";

    private final ImageView[] cardViews = new ImageView[4];
    private final Label[] slotValueLabels = new Label[4];

    private final Label pointsLabel = new Label("Points: 0");
    private final Label statusLabel = new Label("Waiting for input...");
    private final TextField expressionField = new TextField();

    private final int[] cardValues = new int[4];

    private final List<Card> deck = new ArrayList<>();
    private final Random rng = new Random();

    private int points = 0;

    @Override
    public void start(Stage stage)
    {
        buildDeck();

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #f6f7fb; -fx-font-size: 14px;");

        Pane gamePage = buildGamePage(root);
        Pane rulesPage = buildRulesPage(root);

        rulesPage.setVisible(false);
        rulesPage.setManaged(false);

        root.getChildren().addAll(gamePage, rulesPage);

        dealNewHand();

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

        HBox cardsRow = new HBox(16, createCardSlot(0), createCardSlot(1), createCardSlot(2), createCardSlot(3));
        cardsRow.setAlignment(Pos.CENTER);

        pointsLabel.setOpacity(0.75);

        VBox centerArea = new VBox(14, cardsRow, pointsLabel);
        centerArea.setAlignment(Pos.CENTER);
        centerArea.setPadding(new Insets(18));

        Label enterLabel = new Label("Enter expression:");
        enterLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        expressionField.setPromptText("Example: (8-4)*(7-1)");
        HBox.setHgrow(expressionField, Priority.ALWAYS);

        Button verifyBtn = new Button("Verify");
        verifyBtn.setDefaultButton(true);

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> dealNewHand());

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

        verifyBtn.setOnAction(e -> verifyExpression());

        BorderPane layout = new BorderPane();
        layout.setTop(header);
        layout.setCenter(centerArea);
        layout.setBottom(bottomArea);
        layout.setStyle("-fx-background-color: transparent;");
        return layout;
    }

    private Pane buildRulesPage(StackPane root) {
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

    private VBox createCardSlot(int i)
    {
        ImageView iv = new ImageView();
        iv.setFitWidth(120);
        iv.setFitHeight(170);
        iv.setPreserveRatio(true);

        Label valueLabel = new Label("?");
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        StackPane picture = new StackPane(iv);
        picture.setMinSize(132, 182);
        picture.setPadding(new Insets(8));
        picture.setStyle
                ("""
                -fx-background-color: white;
                -fx-border-color: #e6e8ef;
                -fx-border-radius: 16;
                -fx-background-radius: 16;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.10), 10, 0.25, 0, 3);
                """);

        VBox slot = new VBox(8, picture, valueLabel);
        slot.setAlignment(Pos.CENTER);

        cardViews[i] = iv;
        slotValueLabels[i] = valueLabel;
        return slot;
    }

    private void buildDeck()
    {
        deck.clear();
        for (Suit s : Suit.values())
        {
            for (Rank r : Rank.values()) {deck.add(new Card(r, s));}
        }
    }

    private void dealNewHand()
    {
        Collections.shuffle(deck, rng);

        for (int i = 0; i < 4; i++)
        {
            Card c = deck.get(i);
            cardValues[i] = c.rank.value;
            slotValueLabels[i].setText(c.rank.display + " (" + c.rank.value + ")");
            Image img = loadCardImage(c);
            cardViews[i].setImage(img);
        }

        statusLabel.setText("Dealt new cards.");
    }

    private Image loadCardImage(Card c)
    {
        String file = CARD_DIR + c.rank.fileKey + "_of_" + c.suit.fileKey + ".png";
        var url = getClass().getResource(file);

        if (url == null)
        {
            statusLabel.setText("Missing image: " + file + " (check src/main/resources/cards/...)");
            return null;
        }
        return new Image(url.toExternalForm());
    }

    private void verifyExpression()
    {
        String expr = expressionField.getText();
        if (expr == null) {expr = "";}
        expr = expr.trim();

        if (expr.isEmpty())
        {
            statusLabel.setText("Enter an expression first.");
            return;
        }

        List<Integer> used;
        double result;

        try
        {
            List<Token> tokens = tokenize(expr);
            used = extractNumbersUsed(tokens);

            if (used.size() != 4)
            {
                statusLabel.setText("You must use exactly 4 numbers.");
                return;
            }

            if (!sameMultiset(used, cardValues))
            {
                statusLabel.setText("Numbers do not match the cards.");
                return;
            }

            List<Token> rpn = toRpn(tokens);
            result = evalRpn(rpn);
        }
        catch (RuntimeException ex)
        {
            statusLabel.setText("Invalid expression.");
            return;
        }

        if (Math.abs(result - 24.0) < 0.000001)
        {
            points += 24;
            pointsLabel.setText("Points: " + points);
            statusLabel.setText("Correct. +24 points.");
        }
        else
        {
            statusLabel.setText("Result = " + result);
        }
    }

    private boolean sameMultiset(List<Integer> used, int[] cards)
    {
        int[] a = new int[used.size()];
        for (int i = 0; i < used.size(); i++) {a[i] = used.get(i);}

        int[] b = Arrays.copyOf(cards, cards.length);

        Arrays.sort(a);
        Arrays.sort(b);

        return Arrays.equals(a, b);
    }

    private List<Integer> extractNumbersUsed(List<Token> tokens)
    {
        List<Integer> nums = new ArrayList<>();
        for (Token t : tokens)
        {
            if (t.type == TokenType.NUM)
            {
                int v = (int)Math.round(t.value);
                nums.add(Math.abs(v));
            }
        }
        return nums;
    }

    private List<Token> tokenize(String raw)
    {
        String s = raw.replaceAll("\\s+", "");
        if (s.isEmpty()) {throw new RuntimeException("Expression is empty.");}

        List<Token> out = new ArrayList<>();
        int i = 0;

        while (i < s.length())
        {
            char c = s.charAt(i);

            if (Character.isDigit(c))
            {
                int j = i;
                while (j < s.length() && Character.isDigit(s.charAt(j))) {j++;}
                out.add(Token.num(Double.parseDouble(s.substring(i, j))));
                i = j;
                continue;
            }

            if (c == '(') {out.add(Token.lparen()); i++; continue;}
            if (c == ')') {out.add(Token.rparen()); i++; continue;}

            if (c == '+' || c == '*' || c == '/')
            {
                out.add(Token.op(c));
                i++;
                continue;
            }

            if (c == '-')
            {
                boolean unary = out.isEmpty() || out.get(out.size() - 1).type == TokenType.OP || out.get(out.size() - 1).type == TokenType.LPAREN;

                if (unary)
                {
                    if (i + 1 < s.length() && Character.isDigit(s.charAt(i + 1)))
                    {
                        int j = i + 1;
                        while (j < s.length() && Character.isDigit(s.charAt(j))) {j++;}
                        out.add(Token.num(Double.parseDouble(s.substring(i, j))));
                        i = j;
                        continue;
                    }

                    out.add(Token.num(0));
                    out.add(Token.op('-'));
                    i++;
                    continue;
                }

                out.add(Token.op('-'));
                i++;
                continue;
            }

            throw new RuntimeException("Invalid character: " + c);
        }

        return out;
    }

    private List<Token> toRpn(List<Token> tokens)
    {
        List<Token> output = new ArrayList<>();
        Deque<Token> stack = new ArrayDeque<>();

        for (Token t : tokens)
        {
            if (t.type == TokenType.NUM)
            {
                output.add(t);
                continue;
            }

            if (t.type == TokenType.OP)
            {
                while (!stack.isEmpty() && stack.peek().type == TokenType.OP && precedence(stack.peek().op) >= precedence(t.op))
                {
                    output.add(stack.pop());
                }
                stack.push(t);
                continue;
            }

            if (t.type == TokenType.LPAREN)
            {
                stack.push(t);
                continue;
            }

            if (t.type == TokenType.RPAREN)
            {
                while (!stack.isEmpty() && stack.peek().type != TokenType.LPAREN)
                {
                    output.add(stack.pop());
                }

                if (stack.isEmpty() || stack.peek().type != TokenType.LPAREN) {throw new RuntimeException("Mismatched parentheses.");}
                stack.pop();
                continue;
            }
        }

        while (!stack.isEmpty())
        {
            Token t = stack.pop();
            if (t.type == TokenType.LPAREN) {throw new RuntimeException("Mismatched parentheses.");}
            output.add(t);
        }

        return output;
    }

    private int precedence(char op)
    {
        if (op == '*' || op == '/') {return 2;}
        return 1;
    }

    private double evalRpn(List<Token> rpn)
    {
        Deque<Double> st = new ArrayDeque<>();

        for (Token t : rpn)
        {
            if (t.type == TokenType.NUM)
            {
                st.push(t.value);
                continue;
            }

            if (t.type == TokenType.OP)
            {
                if (st.size() < 2) {throw new RuntimeException("Bad expression.");}

                double b = st.pop();
                double a = st.pop();
                double r;

                if (t.op == '+') {r = a + b;}
                else if (t.op == '-') {r = a - b;}
                else if (t.op == '*') {r = a * b;}
                else if (t.op == '/')
                {
                    if (Math.abs(b) < 0.000000000001) {throw new RuntimeException("Division by zero.");}
                    r = a / b;
                }
                else {throw new RuntimeException("Unknown operator: " + t.op);}

                st.push(r);
                continue;
            }

            throw new RuntimeException("Bad expression.");
        }

        if (st.size() != 1) {throw new RuntimeException("Bad expression.");}
        return st.pop();
    }

    private enum TokenType {NUM, OP, LPAREN, RPAREN}

    private static class Token
    {
        final TokenType type;
        final double value;
        final char op;

        private Token(TokenType type, double value, char op)
        {
            this.type = type;
            this.value = value;
            this.op = op;
        }

        static Token num(double v) {return new Token(TokenType.NUM, v, '\0');}
        static Token op(char c) {return new Token(TokenType.OP, 0, c);}
        static Token lparen() {return new Token(TokenType.LPAREN, 0, '\0');}
        static Token rparen() {return new Token(TokenType.RPAREN, 0, '\0');}
    }

    private enum Suit
    {
        CLUBS("clubs"),
        DIAMONDS("diamonds"),
        HEARTS("hearts"),
        SPADES("spades");

        final String fileKey;
        Suit(String fileKey) { this.fileKey = fileKey; }
    }

    private enum Rank
    {
        ACE("ace", "A", 1),
        TWO("2", "2", 2),
        THREE("3", "3", 3),
        FOUR("4", "4", 4),
        FIVE("5", "5", 5),
        SIX("6", "6", 6),
        SEVEN("7", "7", 7),
        EIGHT("8", "8", 8),
        NINE("9", "9", 9),
        TEN("10", "10", 10),
        JACK("jack", "J", 11),
        QUEEN("queen", "Q", 12),
        KING("king", "K", 13);

        final String fileKey;
        final String display;
        final int value;

        Rank(String fileKey, String display, int value)
        {
            this.fileKey = fileKey;
            this.display = display;
            this.value = value;
        }
    }

    private static class Card
    {
        final Rank rank;
        final Suit suit;
        Card(Rank rank, Suit suit) { this.rank = rank; this.suit = suit; }
    }

    public static void main(String[] args) {launch(args);}
}