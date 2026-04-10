module FoundationsF25 {
	requires javafx.controls;
	requires java.sql;
	requires javafx.base;
	requires javafx.graphics;
	
	opens applicationMain to javafx.graphics, javafx.fxml;
}