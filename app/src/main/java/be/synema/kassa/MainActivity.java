package be.synema.kassa;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class MainActivity extends Activity {

    // Constants used by the app
    private static final int MAX_TICKETS = 8;
    private static final int MAX_PAYMENTS = 12;

    // Variables used by the app
    private float amountToPay = 0f;
    private float amountGot = 0f;
    private int lastPayment = 0;
    private int activeSetting = -1;
    private float settingsPrice;
    private int settingsColor;
    private int addedColors = 0;

    // Arrays used by the app
    TicketInfo ticket[] = new TicketInfo[MAX_TICKETS];
    PaymentInfo payment[] = new PaymentInfo[MAX_PAYMENTS];

    private class TicketInfo {
        // Store all information regarding tickets
        private float price;
        private int colour;
        private boolean active;
        private int amount;
        Button priceButton;
        Button amountButton;
        Button settingsButton;
        TableRow ticketRow;

        TicketInfo(){
            price = 0.00f;
            colour = Color.GRAY;
            active = FALSE;
            amount = 0;
        }

        void setPrice(float price) {
            this.price = price;
            if (priceButton != null) {
                String textPrice = "€ ";
                textPrice += String.format("%.2f", settingsPrice);
                priceButton.setText(textPrice);
                settingsButton.setText(textPrice);
            }
        }

        float getPrice() {
            return price;
        }

        public void setColor(int colour) {
            this.colour = colour;
            if (priceButton != null) {
                priceButton.setBackgroundColor(colour);
                amountButton.setBackgroundColor(colour);
                settingsButton.setBackgroundColor(colour);
            }
        }

        public int getColor() {
            return colour;
        }

        boolean isActive() {
            return active;
        }

        void setActive() {
            this.active = TRUE;
            if (ticketRow!=null) {
                ticketRow.setVisibility(View.VISIBLE);
            }
        }

        void setInactive() {
            this.active = FALSE;
            if (ticketRow!=null) {
                ticketRow.setVisibility(View.GONE);
            }
        }

        int getAmount() {
            return amount;
        }

        void incAmount() {
            this.amount = ++amount;
            this.amountButton.setText("" + amount);
        }

        void decAmount() {
            this.amount = --amount;
            this.amountButton.setText("" + amount);
        }

        void resetAmount() {
            this.amount = 0;
            this.amountButton.setText("0");
        }

        void setPriceButton(Button button) {
            this.priceButton = button;
        }

        void setAmountButton(Button button) {
            this.amountButton = button;
        }

        void setSettingsButton(Button settingsButton) {
            this.settingsButton = settingsButton;
        }

        void activeSettingsButton() {
            settingsButton.setBackgroundColor(Color.BLACK);
        }

        void deactiveSettingsButton() {
            settingsButton.setBackgroundColor(colour);
        }

        void setTicketRow(TableRow ticketRow) {
            this.ticketRow = ticketRow;
        }
    }

    private class PaymentInfo {
        // Store all information regarding payments
        private int counter;
        Button payButton;
        Button counterButton;

        PaymentInfo(){
            counter = 0;
        }

        public int getCounter() {
            return counter;
        }

        void incCounter() {
            this.counter = ++counter;
            this.counterButton.setText("" + counter);
        }

        void decCounter() {
            this.counter = --counter;
            this.counterButton.setText("" + counter);
        }

        void resetCounter() {
            this.counter = 0;
            this.counterButton.setText("0");
        }

        void setPayButton(Button payButton) {
            this.payButton = payButton;
        }

        void setCounterButton(Button counterButton) {
            this.counterButton = counterButton;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Initialize
        initButtons();
        initTicketInfo();

        // Create buttons for tickets
        createTicketButtons();
        createSettingsButtons();

        // Create buttons for payment
        createPaymentButton(50f, 1);
        createPaymentButton(20f, 2);
        createPaymentButton(10f, 3);
        createPaymentButton(5f, 1);
        createPaymentButton(2f, 2);
        createPaymentButton(1f, 3);
        createPaymentButton(0.5f, 1);
        createPaymentButton(0.2f, 2);
        createPaymentButton(0.1f, 3);

        // Add available colors for the buttons
        addNewColor(Color.GREEN);
        addNewColor(Color.BLUE);
        addNewColor(Color.RED);
        addNewColor(Color.YELLOW);
        addNewColor(Color.WHITE);
        addNewColor(Color.MAGENTA);
        addNewColor(Color.GRAY);
        addNewColor(Color.CYAN);
        addNewColor(Color.rgb(255,175,175)); // PINK
        addNewColor(Color.rgb(255,200,0)); // ORANGE
        addNewColor(Color.rgb(192,192,192)); // LIGHT GRAY
        addNewColor(Color.rgb(153,153,255)); // MAUVE
        addNewColor(Color.rgb(229,204,255));  // PURPLE
        addNewColor(Color.rgb(102, 178, 255)); // LIGHT BLUE
        addNewColor(Color.rgb(204,255,153)); // LIGHT GREEN
    }

    private void addNewColor(final int newColor) {
        // Create a new row
        TableRow tableRow = new TableRow(this);

        // Rows are all equally high and scale with available space
        TableLayout.LayoutParams tableRowParams = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.MATCH_PARENT,
                1.0f);

        // define margins for the rows
        int leftMargin = 0;
        int topMargin = 0;
        int rightMargin = 0;
        int bottomMargin = 0;

        tableRowParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
        tableRow.setLayoutParams(tableRowParams);

        // Select the table to add the row
        TableLayout table;
        switch (addedColors%5) {
            case 0:
                table = (TableLayout) findViewById(R.id.colorScreen1);
                break;
            case 1:
                table = (TableLayout) findViewById(R.id.colorScreen2);
                break;
            case 2:
                table = (TableLayout) findViewById(R.id.colorScreen3);
                break;
            case 3:
                table = (TableLayout) findViewById(R.id.colorScreen4);
                break;
            default:
                table = (TableLayout) findViewById(R.id.colorScreen5);
                break;
        }
        addedColors++;

        // Add the row
        table.addView(tableRow);

        // Create a button for this color
        Button colorButton = new Button(this);

        // Buttons scale with available space
        TableRow.LayoutParams buttonParams = new TableRow.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.MATCH_PARENT,
                1.0f);

        // define margins for this button
        leftMargin = 0;
        topMargin = 0;
        rightMargin = 0;
        bottomMargin = 0;

        buttonParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
        colorButton.setLayoutParams(buttonParams);

        // Set text and colors
        colorButton.setText("€");
        colorButton.setTextSize(20f);
        colorButton.setPadding (0, 0, 0, 0);
        colorButton.setBackgroundColor(newColor);
        colorButton.setTextColor(Color.BLACK);

        // Define actions for button
        final Button buttonSettings = (Button) findViewById(R.id.buttonToChange);
        colorButton.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    buttonSettings.setBackgroundColor(newColor);
                    settingsColor = newColor;
                }
            });

        // Add button to the row
        tableRow.addView(colorButton);
    }

    private void initButtons() {
        // Set action for reset button
        Button button = (Button) findViewById(R.id.buttonReset);
        button.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTickets();
            }
        });

        // Set action for settings button
        button = (Button) findViewById(R.id.buttonSettings);
        button.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout screenMain = (LinearLayout) findViewById(R.id.screenRight);
                LinearLayout screenSettings = (LinearLayout) findViewById(R.id.screenSettings);
                screenMain.setVisibility(View.GONE);
                screenSettings.setVisibility(View.VISIBLE);
            }
        });

        // Set action for return button
        button = (Button) findViewById(R.id.buttonReturn);
        button.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout screenMain = (LinearLayout) findViewById(R.id.screenRight);
                LinearLayout screenSettings = (LinearLayout) findViewById(R.id.screenSettings);
                screenMain.setVisibility(View.VISIBLE);
                screenSettings.setVisibility(View.GONE);
            }
        });

        // Set action for set price button
        button = (Button) findViewById(R.id.buttonPrice);
        final EditText newPrice = (EditText) findViewById(R.id.editPrice);
        final Button buttonSettings = (Button) findViewById(R.id.buttonToChange);
        button.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Set the new amount on the settings button
                if (newPrice.getText().length()>0) {
                    settingsPrice = Float.parseFloat(newPrice.getText().toString());
                    String textPrice = "€ ";
                    textPrice += String.format("%.2f", settingsPrice);
                    buttonSettings.setText(textPrice);
                }
            }
        });

        // Set action for save changes button
        button = (Button) findViewById(R.id.buttonSave);
        button.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout screenWork = (LinearLayout) findViewById(R.id.screenSettingsWork);
                screenWork.setVisibility(View.GONE);
                ticket[activeSetting].deactiveSettingsButton();
                newPrice.setText("");
                ticket[activeSetting].setColor(settingsColor);
                ticket[activeSetting].setPrice(settingsPrice);

                // Set preferences file
                SharedPreferences sharedPref = getSharedPreferences("be.synema.kassa.settings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                // Write changes to preferences file
                editor.putInt("Color" + activeSetting, settingsColor);
                editor.putFloat("Price" + activeSetting, settingsPrice);
                editor.apply();
            }
        });

        // Set action for cancel button
        button = (Button) findViewById(R.id.buttonCancel);
        button.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout screenWork = (LinearLayout) findViewById(R.id.screenSettingsWork);
                screenWork.setVisibility(View.GONE);
                ticket[activeSetting].deactiveSettingsButton();
                newPrice.setText("");
            }
        });
    }

    private void initTicketInfo() {
        // Set preferences file
        SharedPreferences sharedPref = getSharedPreferences("be.synema.kassa.settings", Context.MODE_PRIVATE);

        // Set default values for the tickets
        for (int count = 0; count < MAX_TICKETS; count++) {
            ticket[count] = new TicketInfo();
            switch (count){
            case 0:
                ticket[count].setPrice(sharedPref.getFloat("Price" + count, 1.40f));
                ticket[count].setColor(sharedPref.getInt("Color" + count, Color.GREEN));
                if (sharedPref.getBoolean("Active" + count, TRUE)) {
                    ticket[count].setActive();
                }
                break;
            case 1:
                ticket[count].setPrice(sharedPref.getFloat("Price" + count, 1.60f));
                ticket[count].setColor(sharedPref.getInt("Color" + count, Color.BLUE));
                if (sharedPref.getBoolean("Active" + count, TRUE)) {
                    ticket[count].setActive();
                }
                break;
            case 2:
                ticket[count].setPrice(sharedPref.getFloat("Price" + count, 2.20f));
                ticket[count].setColor(sharedPref.getInt("Color" + count, Color.RED));
                if (sharedPref.getBoolean("Active" + count, TRUE)) {
                    ticket[count].setActive();
                }
                break;
            case 3:
                ticket[count].setPrice(sharedPref.getFloat("Price" + count, 3.00f));
                ticket[count].setColor(sharedPref.getInt("Color" + count, Color.YELLOW));
                if (sharedPref.getBoolean("Active" + count, TRUE)) {
                    ticket[count].setActive();
                }
                break;
            case 4:
                ticket[count].setPrice(sharedPref.getFloat("Price" + count, 6.00f));
                ticket[count].setColor(sharedPref.getInt("Color" + count, Color.WHITE));
                if (sharedPref.getBoolean("Active" + count, FALSE)) {
                    ticket[count].setActive();
                }
                break;
            case 5:
                ticket[count].setPrice(sharedPref.getFloat("Price" + count, 1.10f));
                ticket[count].setColor(sharedPref.getInt("Color" + count, Color.MAGENTA));
                if (sharedPref.getBoolean("Active" + count, FALSE)) {
                    ticket[count].setActive();
                }
                break;
            case 6:
                ticket[count].setPrice(sharedPref.getFloat("Price" + count, 1.50f));
                ticket[count].setColor(sharedPref.getInt("Color" + count, Color.GRAY));
                if (sharedPref.getBoolean("Active" + count, FALSE)) {
                    ticket[count].setActive();
                }
                break;
            case 7:
                ticket[count].setPrice(0.20f);
                ticket[count].setColor(Color.CYAN);
                if (sharedPref.getBoolean("Active" + count, FALSE)) {
                    ticket[count].setActive();
                }
                break;
            default:
                ticket[count].setPrice(sharedPref.getFloat("Price" + count, 0.00f));
                ticket[count].setColor(sharedPref.getInt("Color" + count, Color.GRAY));
                if (sharedPref.getBoolean("Active" + count, FALSE)) {
                    ticket[count].setActive();
                }
                break;
            }
        }
    }

    private void createTicketButtons() {
        // Get the table where buttons should be added
        TableLayout table = (TableLayout) findViewById(R.id.screenTicketButtons);

        // Create rows of buttons for the tickets
        for (int row = 0; row < MAX_TICKETS; row++) {
            final int ROW_NUM = row;

            final TableRow tableRow = new TableRow(this);
            ticket[row].setTicketRow(tableRow);

            // Rows are all equally high and scale with available space
            TableLayout.LayoutParams tableRowParams = new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT,
                    1.0f);

            // define margins for the rows
            int leftMargin = 0;
            int topMargin = 0;
            int rightMargin = 2;
            int bottomMargin = 6;

            tableRowParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
            tableRow.setLayoutParams(tableRowParams);

            //Hide the row if not active
            if (!ticket[row].isActive()){
                tableRow.setVisibility(View.GONE);
            }

            // Add the row
            table.addView(tableRow);

            // Add the button with the ticket amount
            Button priceButton = new Button(this);

            // Buttons scale with available space
             TableRow.LayoutParams buttonParams = new TableRow.LayoutParams(
                     TableLayout.LayoutParams.MATCH_PARENT,
                     TableLayout.LayoutParams.MATCH_PARENT,
                     1.0f);

            // define margins for this button
            leftMargin = 0;
            topMargin = 0;
            rightMargin = 3;
            bottomMargin = 0;

             buttonParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
             priceButton.setLayoutParams(buttonParams);

             // Set text and colors
             String textPrice ="€ ";
             textPrice += String.format("%.2f", ticket[row].getPrice());
             priceButton.setText(textPrice);
             priceButton.setBackgroundColor(ticket[row].getColor());
             priceButton.setTextColor(Color.BLACK);
             priceButton.setTextSize(40f);
             priceButton.setPadding (0, 0, 0, 0);

             // Increase ticket amount when button is clicked
             priceButton.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     ticket[ROW_NUM].incAmount();
                     amountToPay += ticket[ROW_NUM].getPrice();
                     refreshPayment();
                 }
             });

             // Add this button and store it
            tableRow.addView(priceButton);
            ticket[row].setPriceButton(priceButton);

            // Add the button with the ticket counter
            Button amountButton = new Button (this);
            ticket[row].setAmountButton(amountButton);

            // define margins for this button
            leftMargin = 3;
            topMargin = 0;
            rightMargin = 0;
            bottomMargin = 0;

            buttonParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
            amountButton.setLayoutParams(buttonParams);

            // Set text and colors
            amountButton.setText("" + ticket[row].getAmount());
            amountButton.setBackgroundColor(ticket[row].getColor());
            amountButton.setTextColor(Color.BLACK);
            amountButton.setTextSize(40f);
            amountButton.setPadding (0, 0, 0, 0);

            // Decrease ticket amount when button is clicked
            amountButton.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                    ticket[ROW_NUM].decAmount();
                     amountToPay -= ticket[ROW_NUM].getPrice();
                     refreshPayment();
                 }
            });

             // Add this button
            tableRow.addView(amountButton);
            ticket[row].setAmountButton(amountButton);
          }
    }

    @SuppressLint("DefaultLocale")
    private void createSettingsButtons() {
        // Get the tables where buttons should be added
        TableLayout tableLeft = (TableLayout) findViewById(R.id.screenSettingsLeft);
        TableLayout tableRight = (TableLayout) findViewById(R.id.screenSettingsRight);

        // Create rows of buttons for the tickets
        for (int row = 0; row < MAX_TICKETS; row++) {
            final int ROW_NUM = row;

            TableRow tableRow = new TableRow(this);

            // Rows are all equally high and scale with available space
            TableLayout.LayoutParams tableRowParams = new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT,
                    1.0f);

            // define margins for the rows
            int leftMargin = 0;
            int topMargin = 0;
            int rightMargin = 0;
            int bottomMargin = 0;

            tableRowParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
            tableRow.setLayoutParams(tableRowParams);

            // Add the button with the ticket amount
            final Button priceButton = new Button(this);

            // Buttons scale with available space
            TableRow.LayoutParams buttonParams = new TableRow.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT,
                    1.0f);

            // define margins for this button
            leftMargin = 10;
            topMargin = 5;
            rightMargin = 0;
            bottomMargin = 5;

            buttonParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
            priceButton.setLayoutParams(buttonParams);

            // Set text and colors
            String textPrice = "€ ";
            textPrice += String.format("%.2f", ticket[row].getPrice());
            priceButton.setText(textPrice);
            priceButton.setBackgroundColor(ticket[row].getColor());
            priceButton.setTextColor(Color.BLACK);
            priceButton.setTextSize(40f);
            priceButton.setPadding(0, 0, 0, 0);

            //Set the button for the settings
            priceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (activeSetting >= 0) {
                        ticket[activeSetting].deactiveSettingsButton();
                    }
                    activeSetting = ROW_NUM;
                    ticket[activeSetting].activeSettingsButton();
                    initSettingsButton();
                }
            });

            // Add the checkbox for the button
            final CheckBox activeCheck = new CheckBox(this);

            // Set the value for the checkbox
            activeCheck.setChecked(ticket[row].isActive());

            // Szt action for value changes
            activeCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Set preferences file
                    SharedPreferences sharedPref = getSharedPreferences("be.synema.kassa.settings", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();

                    // Write changes to preferences file
                    editor.putBoolean("Active" + ROW_NUM, isChecked);
                    editor.apply();

                    if (isChecked) {
                        ticket[ROW_NUM].setActive();
                    }
                    else {
                        ticket[ROW_NUM].setInactive();
                    }
                }
            });

            // Add the row, button and checkbox
            if (row < MAX_TICKETS / 2) {
                tableLeft.addView(tableRow);
                tableRow.addView(priceButton);
                tableRow.addView(activeCheck);
            } else {
                tableRight.addView(tableRow);
                tableRow.addView(activeCheck);
                tableRow.addView(priceButton);
            }

            // Store the button
            ticket[row].setSettingsButton(priceButton);
        }
    }

    @SuppressLint("DefaultLocale")
    private void initSettingsButton() {
        // Connect to the settings work area and the button
        LinearLayout areaSettings = (LinearLayout) findViewById(R.id.screenSettingsWork);
        Button changeButton = (Button) findViewById(R.id.buttonToChange);

        // Set the area visible is a ticket is active, if not set it gone
        if (activeSetting >= 0) {
            areaSettings.setVisibility(View.VISIBLE);

            // Set values for price and color settings ticket
            settingsPrice = ticket[activeSetting].getPrice();
            settingsColor = ticket[activeSetting].getColor();

            // Set the button to the values of the ticket
            String textPrice = "€ ";
            textPrice += String.format("%.2f", settingsPrice);
            changeButton.setText(textPrice);
            changeButton.setBackgroundColor(settingsColor);
            changeButton.setTextColor(Color.BLACK);
            changeButton.setTextSize(40f);
            changeButton.setPadding(0, 0, 0, 0);
        }
        else {
            areaSettings.setVisibility(View.GONE);
        }
    }

    private void createPaymentButton(final float payAmount, int column) {
        // Set table based on requested column
        TableLayout table;
        switch (column) {
            case 1:
                table = (TableLayout) findViewById(R.id.payScreen1);
                break;
            case 2:
                table = (TableLayout) findViewById(R.id.payScreen2);
                break;
            case 3:
                table = (TableLayout) findViewById(R.id.payScreen3);
                break;
            default:
                table = (TableLayout) findViewById(R.id.payScreen1);
                break;
        }

        // Create a new row in the column for the requested amount
        TableRow tableRow = new TableRow(this);
        // Rows are all equally high and scale with available space
        TableLayout.LayoutParams tableRowParams = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.MATCH_PARENT,
                1.0f);

        // define margins for the rows
        int leftMargin = 0;
        int topMargin = 0;
        int rightMargin = 0;
        int bottomMargin = 0;

        tableRowParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
        tableRow.setLayoutParams(tableRowParams);

        // Add the row
        table.addView(tableRow);

        // Create the button with the payment counter
        final Button countButton = new Button(this);

        // Buttons scale with available space
        TableRow.LayoutParams buttonParams = new TableRow.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.MATCH_PARENT,
                1.0f);

        // define margins for this button
        leftMargin = 0;
        topMargin = 0;
        rightMargin = 0;
        bottomMargin = 0;

        buttonParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
        countButton.setLayoutParams(buttonParams);

        // Set text and colors
        countButton.setText("0");
        countButton.setTextSize(20f);
        countButton.setPadding (0, 0, 0, 0);

        // Store button
        final int payNum = lastPayment++;
        payment[payNum] = new PaymentInfo();
        payment[payNum].setCounterButton(countButton);

        // Decrease payment amount when button is clicked
        countButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payment[payNum].decCounter();
                amountGot -= payAmount;
                refreshPayment();
            }
        });

        // Create the button with the payment amount
        Button payButton = new Button(this);

        // Buttons scale with available space
        buttonParams = new TableRow.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.MATCH_PARENT,
                1.0f);

        // define margins for this button
        leftMargin = 0;
        topMargin = 0;
        rightMargin = 0;
        bottomMargin = 0;

        buttonParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
        payButton.setLayoutParams(buttonParams);

        // Set text and colors
        String textPrice ="€ ";
        if (payAmount%1 == 0) {
            textPrice += String.format("%.0f", payAmount);
        }
        else {
            textPrice += String.format("%.2f", payAmount);
        }
        payButton.setText(textPrice);
        payButton.setTextSize(20f);
        payButton.setPadding (0, 0, 0, 0);

        // Store button
        payment[payNum].setPayButton(payButton);

        // Increase payment amount when button is clicked
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payment[payNum].incCounter();
                amountGot += payAmount;
                refreshPayment();
            }
        });

        // Add both buttons to the row
        tableRow.addView(payButton);
        tableRow.addView(countButton);
    }

    private void refreshPayment() {
        // Set total amount to pay
        String textAmount = "€ ";
        textAmount += String.format("%.2f", amountToPay);
        TextView textToPay = (TextView) findViewById(R.id.amountToPay);
        textToPay.setText(textAmount);

        // Set amount already gotten
        textAmount = "€ ";
        textAmount += String.format("%.2f", amountGot);
        TextView textGotPay = (TextView) findViewById(R.id.amountGotPay);
        textGotPay.setText(textAmount);

        // Set amount left to pay
        textAmount = "€ ";
        textAmount += String.format("%.2f", amountToPay-amountGot);
        TextView textLeftPay = (TextView) findViewById(R.id.amountLeftPay);
        textLeftPay.setText(textAmount);
        if (amountToPay > amountGot) {
            textLeftPay.setTextColor(Color.RED);
        }
        else {
            textLeftPay.setTextColor(Color.GREEN);
        }
    }

    private void resetTickets(){
        // Set all the tickets to zero
        for (int cnt = 0; cnt < MAX_TICKETS; cnt++){
            ticket[cnt].resetAmount();
        }

        // Set all the payments to zero
        for (int cnt = 0; cnt < lastPayment; cnt++){
            payment[cnt].resetCounter();
        }

        // Set all the payment amounts to zero
        amountToPay = 0f;
        amountGot = 0f;
        refreshPayment();
    }
}
