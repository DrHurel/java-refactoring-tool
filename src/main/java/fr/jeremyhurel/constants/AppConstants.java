package fr.jeremyhurel.constants;

public final class AppConstants {

    private AppConstants() {

    }

    public static final int DEFAULT_METHOD_THRESHOLD = 10;

    public static final double TOP_PERCENTAGE = 0.1;

    public static final int MIN_TOP_ITEMS = 1;

    public static final int COUPLING_DECIMAL_PLACES = 6;

    public static final String JSON_EXTENSION = ".json";
    public static final String DOT_EXTENSION = ".dot";
    public static final String TXT_EXTENSION = ".txt";
    public static final String PUML_EXTENSION = ".puml";
    public static final String NWK_EXTENSION = ".nwk";

    public static final class DotConfig {
        public static final String RANKDIR_LR = "LR";
        public static final String RANKDIR_TB = "TB";
        public static final String NODE_SHAPE_BOX = "box";
        public static final String EDGE_DIR_NONE = "none";
        public static final String COLOR_LIGHTBLUE = "lightblue";
        public static final String COLOR_LIGHTGREEN = "lightgreen";
        public static final String COLOR_LIGHTYELLOW = "lightyellow";
        public static final String COLOR_WHITESMOKE = "WhiteSmoke";
        public static final String COLOR_GRAY = "gray";
        public static final String COLOR_RED = "red";
        public static final String COLOR_ORANGE = "orange";
        public static final String COLOR_BLUE = "blue";

        private DotConfig() {}
    }

    public static final class PlantUMLConfig {
        public static final String COLOR_INTERFACE = "LightBlue";
        public static final String COLOR_ABSTRACT = "LightYellow";
        public static final String COLOR_CONCRETE = "LightGreen";
        public static final String COLOR_PACKAGE_BG = "WhiteSmoke";
        public static final String COLOR_PACKAGE_BORDER = "Gray";

        private PlantUMLConfig() {}
    }

    public static final class CouplingThresholds {
        public static final double VERY_HIGH = 0.1;
        public static final double HIGH = 0.05;
        public static final double MEDIUM = 0.01;

        private CouplingThresholds() {}
    }
}
