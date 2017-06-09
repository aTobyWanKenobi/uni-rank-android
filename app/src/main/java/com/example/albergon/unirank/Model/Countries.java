package com.example.albergon.unirank.Model;

import com.example.albergon.unirank.R;

import java.util.HashMap;
import java.util.Map;

/**
 * This class contains static fields and method useful to store and model countries in the application.
 * It is not instantiable.
 */
public class Countries {

    // private constructor
    private Countries() {}

    public static final Map<String, String> countryMap = createCountryMap();

    public static final Map<String, Integer> commonIconMap = createCommonCountryIconMap();

    private static Map<String, String> createCountryMap() {

        Map<String, String> codeToName = new HashMap<>();
        codeToName.put("AGO" ,"Angola");
        codeToName.put("ALB" ,"Albania");
        codeToName.put("AND" ,"Andorra");
        codeToName.put("ARE" ,"United Arab Emirates");
        codeToName.put("ARG" ,"Argentina");
        codeToName.put("ARM" ,"Armenia");
        codeToName.put("AUS" ,"Australia");
        codeToName.put("AUT" ,"Austria");
        codeToName.put("AZE" ,"Azerbaijan");
        codeToName.put("BDI" ,"Burundi");
        codeToName.put("BEL" ,"Belgium");
        codeToName.put("BEN" ,"Benin");
        codeToName.put("BFA" ,"Burkina Faso");
        codeToName.put("BGD" ,"Bangladesh");
        codeToName.put("BGR" ,"Bulgaria");
        codeToName.put("BHR" ,"Bahrain");
        codeToName.put("BHS" ,"Bahamas");
        codeToName.put("BIH" ,"Bosnia and Herzegovina");
        codeToName.put("BLR" ,"Belarus");
        codeToName.put("BLZ" ,"Belize");
        codeToName.put("BMU" ,"Bermuda");
        codeToName.put("BOL" ,"Bolivia");
        codeToName.put("BRA" ,"Brazil");
        codeToName.put("BRB" ,"Barbados");
        codeToName.put("BRN" ,"Brunei");
        codeToName.put("BTN" ,"Bhutan");
        codeToName.put("BWA" ,"Botswana");
        codeToName.put("CAF" ,"Central African Republic");
        codeToName.put("CAN" ,"Canada");
        codeToName.put("CHE" ,"Switzerland");
        codeToName.put("CHL" ,"Chile");
        codeToName.put("CHN" ,"China");
        codeToName.put("CIV" ,"Côte d'Ivoire");
        codeToName.put("CMR" ,"Cameroon");
        codeToName.put("COD" ,"Democratic Republic of the Congo");
        codeToName.put("COG" ,"Congo");
        codeToName.put("COL" ,"Colombia");
        codeToName.put("CPV" ,"Cape Verde");
        codeToName.put("CRI" ,"Costa Rica");
        codeToName.put("CUB" ,"Cuba");
        codeToName.put("CYP" ,"Cyprus");
        codeToName.put("CZE" ,"Czech Republic");
        codeToName.put("DEU" ,"Germany");
        codeToName.put("DNK" ,"Denmark");
        codeToName.put("DOM" ,"Dominican Republic");
        codeToName.put("DZA" ,"Algeria");
        codeToName.put("ECU" ,"Ecuador");
        codeToName.put("EGY" ,"Egypt");
        codeToName.put("ERI" ,"Eritrea");
        codeToName.put("ESH" ,"Western Sahara");
        codeToName.put("ESP" ,"Spain");
        codeToName.put("EST" ,"Estonia");
        codeToName.put("ETH" ,"Ethiopia");
        codeToName.put("FIN" ,"Finland");
        codeToName.put("FJI" ,"Fiji");
        codeToName.put("FRA" ,"France");
        codeToName.put("GAB" ,"Gabon");
        codeToName.put("GBR" ,"United Kingdom");
        codeToName.put("GEO" ,"Georgia");
        codeToName.put("GHA" ,"Ghana");
        codeToName.put("GIB" ,"Gibraltar");
        codeToName.put("GIN" ,"Guinea");
        codeToName.put("GLP" ,"Guadeloupe");
        codeToName.put("GMB" ,"Gambia");
        codeToName.put("GNB" ,"Guinea-Bissau");
        codeToName.put("GNQ" ,"Equatorial Guinea");
        codeToName.put("GRC" ,"Greece");
        codeToName.put("GRD" ,"Grenada");
        codeToName.put("GRL" ,"Greenland");
        codeToName.put("GTM" ,"Guatemala");
        codeToName.put("GUF" ,"French Guiana");
        codeToName.put("GUM" ,"Guam");
        codeToName.put("GUY" ,"Guyana");
        codeToName.put("HKG" ,"Hong Kong");
        codeToName.put("HND" ,"Honduras");
        codeToName.put("HRV" ,"Croatia");
        codeToName.put("HTI" ,"Haiti");
        codeToName.put("HUN" ,"Hungary");
        codeToName.put("IDN" ,"Indonesia");
        codeToName.put("IND" ,"India");
        codeToName.put("IRL" ,"Ireland");
        codeToName.put("IRN" ,"Iran");
        codeToName.put("IRQ" ,"Iraq");
        codeToName.put("ISL" ,"Iceland");
        codeToName.put("ISR" ,"Israel");
        codeToName.put("ITA" ,"Italy");
        codeToName.put("JAM" ,"Jamaica");
        codeToName.put("JPN" ,"Japan");
        codeToName.put("KAZ" ,"Kazakhstan");
        codeToName.put("KEN" ,"Kenya");
        codeToName.put("KGZ" ,"Kyrgyzstan");
        codeToName.put("KHM" ,"Cambodia");
        codeToName.put("KOR" ,"South Korea");
        codeToName.put("KWT" ,"Kuwait");
        codeToName.put("LBN" ,"Lebanon");
        codeToName.put("LBR" ,"Liberia");
        codeToName.put("LIE" ,"Liechtenstein");
        codeToName.put("LKA" ,"Sri Lanka");
        codeToName.put("LSO" ,"Lesotho");
        codeToName.put("LTU" ,"Lithuania");
        codeToName.put("LUX" ,"Luxembourg");
        codeToName.put("LVA" ,"Latvia");
        codeToName.put("MAC" ,"Macao");
        codeToName.put("MAR" ,"Morocco");
        codeToName.put("MCO" ,"Monaco");
        codeToName.put("MDA" ,"Moldova");
        codeToName.put("MDG" ,"Madagascar");
        codeToName.put("MDV" ,"Maldives");
        codeToName.put("MEX" ,"Mexico");
        codeToName.put("MKD" ,"Macedonia");
        codeToName.put("MLI" ,"Mali");
        codeToName.put("MLT" ,"Malta");
        codeToName.put("MMR" ,"Myanmar");
        codeToName.put("MNE" ,"Montenegro");
        codeToName.put("MNG" ,"Mongolia");
        codeToName.put("MOZ" ,"Mozambique");
        codeToName.put("MRT" ,"Mauritania");
        codeToName.put("MTQ" ,"Martinique");
        codeToName.put("MUS" ,"Mauritius");
        codeToName.put("MWI" ,"Malawi");
        codeToName.put("MYS" ,"Malaysia");
        codeToName.put("NAM" ,"Namibia");
        codeToName.put("NER" ,"Niger");
        codeToName.put("NGA" ,"Nigeria");
        codeToName.put("NIC" ,"Nicaragua");
        codeToName.put("NLD" ,"Netherlands");
        codeToName.put("NOR" ,"Norway");
        codeToName.put("NPL" ,"Nepal");
        codeToName.put("NZL" ,"New Zealand");
        codeToName.put("OMN" ,"Oman");
        codeToName.put("PAK" ,"Pakistan");
        codeToName.put("PAN" ,"Panama");
        codeToName.put("PER" ,"Peru");
        codeToName.put("PHL" ,"Philippines");
        codeToName.put("PNG" ,"Papua New Guinea");
        codeToName.put("POL" ,"Poland");
        codeToName.put("PRI" ,"Puerto Rico");
        codeToName.put("PRK" ,"North Korea");
        codeToName.put("PRT" ,"Portugal");
        codeToName.put("PRY" ,"Paraguay");
        codeToName.put("QAT" ,"Qatar");
        codeToName.put("ROU" ,"Romania");
        codeToName.put("RUS" ,"Russian Federation");
        codeToName.put("RWA" ,"Rwanda");
        codeToName.put("SAU" ,"Saudi Arabia");
        codeToName.put("SDN" ,"Sudan");
        codeToName.put("SEN" ,"Senegal");
        codeToName.put("SGP" ,"Singapore");
        codeToName.put("SLE" ,"Sierra Leone");
        codeToName.put("SLV" ,"El Salvador");
        codeToName.put("SMR" ,"San Marino");
        codeToName.put("SOM" ,"Somalia");
        codeToName.put("SRB" ,"Serbia");
        codeToName.put("SUR" ,"Suriname");
        codeToName.put("SVK" ,"Slovakia");
        codeToName.put("SVN" ,"Slovenia");
        codeToName.put("SWE" ,"Sweden");
        codeToName.put("SWZ" ,"Swaziland");
        codeToName.put("SYC" ,"Seychelles");
        codeToName.put("SYR" ,"Syrian Arab Republic");
        codeToName.put("TCD" ,"Chad");
        codeToName.put("TGO" ,"Togo");
        codeToName.put("THA" ,"Thailand");
        codeToName.put("TJK" ,"Tajikistan");
        codeToName.put("TKM" ,"Turkmenistan");
        codeToName.put("TON" ,"Tonga");
        codeToName.put("TTO" ,"Trinidad and Tobago");
        codeToName.put("TUN" ,"Tunisia");
        codeToName.put("TUR" ,"Turkey");
        codeToName.put("TWN" ,"Taiwan");
        codeToName.put("TZA" ,"Tanzania");
        codeToName.put("UGA" ,"Uganda");
        codeToName.put("UKR" ,"Ukraine");
        codeToName.put("URY" ,"Uruguay");
        codeToName.put("USA" ,"United States");
        codeToName.put("UZB" ,"Uzbekistan");
        codeToName.put("VEN" ,"Venezuela");
        codeToName.put("VNM" ,"Vietnam");
        codeToName.put("WSM" ,"Samoa");
        codeToName.put("YEM" ,"Yemen");
        codeToName.put("ZAF" ,"South Africa");
        codeToName.put("ZMB" ,"Zambia");
        codeToName.put("ZWE" ,"Zimbabwe");

        return codeToName;
    }

    private static Map<String, Integer> createCommonCountryIconMap() {

        Map<String, Integer> codeToIcon = new HashMap<>();
        codeToIcon.put("ARG" , R.drawable.z_icon_argentina);
        codeToIcon.put("AUS" , R.drawable.z_icon_australia);
        codeToIcon.put("BEL" , R.drawable.z_icon_belgium);
        codeToIcon.put("CAN" , R.drawable.z_icon_canada);
        codeToIcon.put("CH" , R.drawable.z_icon_switzerland);
        codeToIcon.put("CN" , R.drawable.z_icon_china);
        codeToIcon.put("DEU" , R.drawable.z_icon_germany);
        codeToIcon.put("DNK" , R.drawable.z_icon_denmark);
        codeToIcon.put("FIN" , R.drawable.z_icon_finland);
        codeToIcon.put("FRA" , R.drawable.z_icon_france);
        codeToIcon.put("GB" , R.drawable.z_icon_united_kingdom);
        codeToIcon.put("HK" , R.drawable.z_icon_hong_kong);
        codeToIcon.put("IRL" , R.drawable.z_icon_ireland);
        codeToIcon.put("JPN" , R.drawable.z_icon_japan);
        codeToIcon.put("KOR" , R.drawable.z_icon_south_korea);
        codeToIcon.put("NLD" , R.drawable.z_icon_netherlands);
        codeToIcon.put("SGP" , R.drawable.z_icon_singapore);
        codeToIcon.put("SWE" , R.drawable.z_icon_sweden);
        codeToIcon.put("TWN" , R.drawable.z_icon_taiwan);
        codeToIcon.put("USA" , R.drawable.z_icon_usa);

        return codeToIcon;
    }


}
