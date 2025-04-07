module com.udacity.security {
    requires com.udacity.imageservice;
    requires com.google.gson;
    exports com.udacity.security.service;
    exports com.udacity.security.application;
    opens com.udacity.security.data to com.google.gson; // For Gson reflection
}