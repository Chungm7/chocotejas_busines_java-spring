package com.example.sistema_venta_chocotejas.Enum;

public enum IconoRedSocial {
    FACEBOOK("bi-facebook"),
    TWITTER("bi-twitter"),
    INSTAGRAM("bi-instagram"),
    YOUTUBE("bi-youtube"),
    WHATSAPP("bi-whatsapp"),
    TIKTOK("bi-tiktok");

    private final String claseBootstrap;

    IconoRedSocial(String claseBootstrap) {
        this.claseBootstrap = claseBootstrap;
    }

    public String getClaseBootstrap() {
        return claseBootstrap;
    }

    public String getClaseCompleta() {
        return "bi " + claseBootstrap;
    }
}