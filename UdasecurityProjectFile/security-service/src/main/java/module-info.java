module security.service {
    requires image.service;
    requires com.google.gson;
    requires java.desktop;
    exports com.udacity.security.service;
    exports com.udacity.security.data;
    exports com.udacity.security.application;
    opens com.udacity.security.data to com.google.gson;
}