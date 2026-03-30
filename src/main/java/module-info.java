module bikram.businessmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.cdi;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires jakarta.persistence;
    requires static lombok;
    requires jdk.jfr;
    requires java.naming;
    requires org.hibernate.orm.core;
    requires javafx.graphics;
    requires jakarta.transaction;
    requires com.fasterxml.jackson.databind;
    requires com.zaxxer.hikari;
    requires org.slf4j;
    requires org.apache.pdfbox;
    requires java.desktop;
    requires javafx.swing;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;


    opens bikram.businessmanager.entity;
    opens bikram.businessmanager.controller to javafx.fxml;
    exports bikram.businessmanager;
    opens bikram.businessmanager.controller.company to javafx.fxml;
    opens bikram.businessmanager.controller.product to javafx.fxml;
    opens bikram.businessmanager.controller.employee to javafx.fxml;
    opens bikram.businessmanager.controller.dashbord to javafx.fxml;
    opens bikram.businessmanager.entity.account;
    exports bikram.businessmanager.utils;
    exports bikram.businessmanager.controller.dashbord;
    exports bikram.businessmanager.controller.acounting to javafx.fxml;
    opens bikram.businessmanager.controller.acounting;
    exports bikram.businessmanager.dto;
    opens bikram.businessmanager.dto to javafx.base;
    opens bikram.businessmanager.utils to com.fasterxml.jackson.databind;
    opens bikram.businessmanager.entity.inventory;
    opens bikram.businessmanager.controller.inventory to javafx.fxml;
    opens bikram.businessmanager.controller.acounting.transition to javafx.fxml;
    opens bikram.businessmanager.controller.counter to javafx.fxml;
    opens bikram.businessmanager.controller.inventory.report.sale_report to javafx.fxml;
    exports bikram.businessmanager.dto.sale;
    opens bikram.businessmanager.dto.sale to javafx.base;
}