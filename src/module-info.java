module FoundationsF25 {
	requires javafx.controls;
	requires java.sql;
	requires javafx.base;
	requires javafx.graphics;
	requires org.junit.jupiter.api;
	
	opens applicationMain to javafx.graphics, javafx.fxml;
	
	opens tester to org.junit.platform.commons;
}