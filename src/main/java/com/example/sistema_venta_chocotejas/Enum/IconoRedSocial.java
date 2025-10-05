package com.example.sistema_venta_chocotejas.Enum;

public enum IconoRedSocial {
    FACEBOOK("bi-facebook"),
    TWITTER("bi-twitter"),
    INSTAGRAM("bi-instagram"),
    LINKEDIN("bi-linkedin"),
    YOUTUBE("bi-youtube"),
    WHATSAPP("bi-whatsapp"),
    TIKTOK("bi-tiktok"),
    PINTEREST("bi-pinterest"),
    SNAPCHAT("bi-snapchat"),
    SPOTIFY("bi-spotify"),
    TELEGRAM("bi-telegram"),
    DISCORD("bi-discord"),
    REDDIT("bi-reddit"),
    GITHUB("bi-github"),
    GOOGLE("bi-google"),
    APPLE("bi-apple"),
    MICROSOFT("bi-microsoft"),
    SHOPIFY("bi-shopify"),
    PAYPAL("bi-paypal"),
    SLACK("bi-slack"),
    BEHANCE("bi-behance"),
    DRIBBLE("bi-dribbble"),
    TWITCH("bi-twitch"),
    VIMEO("bi-vimeo"),
    SKYPE("bi-skype"),
    MESSENGER("bi-messenger"),
    SIGNAL("bi-signal"),
    WE_CHAT("bi-wechat"),
    LINE("bi-line"),
    MEDIUM("bi-medium"),
    WORDPRESS("bi-wordpress"),
    BLOGGER("bi-blogger");

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