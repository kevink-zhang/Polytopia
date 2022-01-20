package org.cis120.polytopia;

import java.awt.*;

public class FontLoader {
    public Font head1;
    public Font head2;
    public Font text1;
    public Font text2;
    public Font text3;
    public Font title1;

    public FontLoader() {
        head1 = new Font("Josefin Sans", Font.BOLD, 24); // in game infobox title
        head2 = new Font("Josefin Sans", Font.BOLD, 30); // main menu button
        text1 = new Font("Josefin Sans", Font.TRUETYPE_FONT, 18); // in game infobox body
        text2 = new Font("Josefin Sans", Font.TRUETYPE_FONT, 12); // circle button label
        text3 = new Font("Josefin Sans", Font.TRUETYPE_FONT, 10); // circle button label
        title1 = new Font("Josefin Sans", Font.BOLD, 50); // main menu button
        System.out.println("Fonts loaded!");
    }
}
