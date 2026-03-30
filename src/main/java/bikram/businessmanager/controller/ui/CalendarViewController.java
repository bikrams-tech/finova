package bikram.businessmanager.controller.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.time.YearMonth;

public class CalendarViewController {

    @FXML private Label monthLabel;
    @FXML private GridPane calendarGrid;

    private YearMonth currentYearMonth;

    @FXML
    public void initialize() {
        currentYearMonth = YearMonth.now();
        drawCalendar();
    }

    @FXML
    private void previousMonth() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        drawCalendar();
    }

    @FXML
    private void nextMonth() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        drawCalendar();
    }

    private void drawCalendar() {

        calendarGrid.getChildren().clear();

        monthLabel.setText(
                currentYearMonth.getMonth() + " " + currentYearMonth.getYear()
        );

        LocalDate firstDay = currentYearMonth.atDay(1);
        int daysInMonth = currentYearMonth.lengthOfMonth();

        int column = firstDay.getDayOfWeek().getValue() % 7;
        int row = 1;

        for (int day = 1; day <= daysInMonth; day++) {

            Button dayButton = new Button(String.valueOf(day));
            dayButton.setPrefSize(40, 40);

            calendarGrid.add(dayButton, column, row);

            column++;
            if (column == 7) {
                column = 0;
                row++;
            }
        }
    }
}