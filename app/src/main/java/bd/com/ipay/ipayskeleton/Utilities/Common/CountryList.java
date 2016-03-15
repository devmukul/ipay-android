package bd.com.ipay.ipayskeleton.Utilities.Common;

import java.util.HashMap;
import java.util.Map;

public class CountryList {

    public final static String[] countryNames = {

            "Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra", "Angola",
            "Anguilla", "Antigua and Barbuda", "Argentina", "Armenia", "Aruba",
            "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain",
            "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin",
            "Bermuda", "Bhutan", "Bolivia", "Bonaire, Sint Eustatius and Saba",
            "Bosnia and Herzegovina", "Botswana", "Brazil",
            "British Indian Ocean Territory", "British Virgin Islands",
            "Brunei", "Bulgaria", "Burkina Faso", "Burundi", "Cambodia",
            "Cameroon", "Canada", "Cape Verde", "Cayman Islands",
            "Central African Republic", "Chad", "Chile", "China",
            "Christmas Island", "Colombia", "Comoros", "Congo", "Cook Islands",
            "Costa Rica", "Croatia", "Cuba", "Curaï¿½ao", "Cyprus",
            "Czech Republic", "Cï¿½te d'Ivoire", "Denmark", "Djibouti",
            "Dominica", "Dominican Republic", "Ecuador", "Egypt",
            "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia",
            "Ethiopia", "Falkland Islands", "Faroe Islands", "Fiji", "Finland",
            "France", "French Guiana", "French Polynesia", "Gabon", "Gambia",
            "Georgia", "Germany", "Ghana", "Gibraltar", "Greece", "Greenland",
            "Grenada", "Guadeloupe", "Guam", "Guatemala", "Guernsey", "Guinea",
            "Guinea-Bissau", "Guyana", "Haiti", "Honduras", "Hong Kong",
            "Hungary", "Iceland", "India", "Indonesia", "Iran", "Iraq",
            "Ireland", "Isle Of Man", "Israel", "Italy", "Jamaica", "Japan",
            "Jersey", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Kuwait",
            "Kyrgyzstan", "Laos", "Latvia", "Lebanon", "Lesotho", "Liberia",
            "Libya", "Liechtenstein", "Lithuania", "Luxembourg", "Macao",
            "Macedonia", "Madagascar", "Malawi", "Malaysia", "Maldives",
            "Mali", "Malta", "Marshall Islands", "Martinique", "Mauritania",
            "Mauritius", "Mayotte", "Mexico", "Micronesia", "Moldova",
            "Monaco", "Mongolia", "Montenegro", "Montserrat", "Morocco",
            "Mozambique", "Myanmar", "Namibia", "Nauru", "Nepal",
            "Netherlands", "New Caledonia", "New Zealand", "Nicaragua",
            "Niger", "Nigeria", "Niue", "Norfolk Island", "North Korea",
            "Northern Mariana Islands", "Norway", "Oman", "Pakistan", "Palau",
            "Palestine", "Panama", "Papua New Guinea", "Paraguay", "Peru",
            "Philippines", "Poland", "Portugal", "Puerto Rico", "Qatar",
            "Reunion", "Romania", "Russia", "Rwanda", "Saint Barthelemy",
            "Saint Helena", "Saint Kitts And Nevis", "Saint Lucia",
            "Saint Martin", "Saint Pierre And Miquelon",
            "Saint Vincent And The Grenadines", "Samoa", "San Marino",
            "Sao Tome And Principe", "Saudi Arabia", "Senegal", "Serbia",
            "Seychelles", "Sierra Leone", "Singapore",
            "Sint Maarten (Dutch part)", "Slovakia", "Slovenia",
            "Solomon Islands", "Somalia", "South Africa", "South Korea",
            "Spain", "Sri Lanka", "Sudan", "Suriname",
            "Svalbard And Jan Mayen", "Swaziland", "Sweden", "Switzerland",
            "Syria", "Taiwan", "Tajikistan", "Tanzania", "Thailand",
            "The Democratic Republic Of Congo", "Timor-Leste", "Togo",
            "Tokelau", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey",
            "Turkmenistan", "Turks And Caicos Islands", "Tuvalu",
            "U.S. Virgin Islands", "Uganda", "Ukraine", "United Arab Emirates",
            "United Kingdom", "United States", "Uruguay", "Uzbekistan",
            "Vanuatu", "Vatican", "Venezuela", "Vietnam", "Wallis And Futuna",
            "Western Sahara", "Yemen", "Zambia", "Zimbabwe",

    };

    public final static String[] countryISOcodes = {

            "AF", "AL", "DZ", "AS", "AD", "AO", "AI", "AG", "AR", "AM", "AW", "AU",
            "AT", "AZ", "BS", "BH", "BD", "BB", "BY", "BE", "BZ", "BJ", "BM",
            "BT", "BO", "BQ", "BA", "BW", "BR", "IO", "VG", "BN", "BG", "BF",
            "BI", "KH", "CM", "CA", "CV", "KY", "CF", "TD", "CL", "CN", "CX",
            "CO", "KM", "CG", "CK", "CR", "HR", "CU", "CW", "CY", "CZ", "CI",
            "DK", "DJ", "DM", "DO", "EC", "EG", "SV", "GQ", "ER", "EE", "ET",
            "FK", "FO", "FJ", "FI", "FR", "GF", "PF", "GA", "GM", "GE", "DE",
            "GH", "GI", "GR", "GL", "GD", "GP", "GU", "GT", "GG", "GN", "GW",
            "GY", "HT", "HN", "HK", "HU", "IS", "IN", "ID", "IR", "IQ", "IE",
            "IM", "IL", "IT", "JM", "JP", "JE", "JO", "KZ", "KE", "KI", "KW",
            "KG", "LA", "LV", "LB", "LS", "LR", "LY", "LI", "LT", "LU", "MO",
            "MK", "MG", "MW", "MY", "MV", "ML", "MT", "MH", "MQ", "MR", "MU",
            "YT", "MX", "FM", "MD", "MC", "MN", "ME", "MS", "MA", "MZ", "MM",
            "NA", "NR", "NP", "NL", "NC", "NZ", "NI", "NE", "NG", "NU", "NF",
            "KP", "MP", "NO", "OM", "PK", "PW", "PS", "PA", "PG", "PY", "PE",
            "PH", "PL", "PT", "PR", "QA", "RE", "RO", "RU", "RW", "BL", "SH",
            "KN", "LC", "MF", "PM", "VC", "WS", "SM", "ST", "SA", "SN", "RS",
            "SC", "SL", "SG", "SX", "SK", "SI", "SB", "SO", "ZA", "KR", "ES",
            "LK", "SD", "SR", "SJ", "SZ", "SE", "CH", "SY", "TW", "TJ", "TZ",
            "TH", "CD", "TL", "TG", "TK", "TO", "TT", "TN", "TR", "TM", "TC",
            "TV", "VI", "UG", "UA", "AE", "GB", "US", "UY", "UZ", "VU", "VA",
            "VE", "VN", "WF", "EH", "YE", "ZM", "ZW" };

    public final static String[] countryDialingCodes = {

            "93", "355", "213", "1", "376", "244", "1", "1", "54", "374", "297", "61",
            "43", "994", "1", "973", "880", "1", "375", "32", "501", "229",
            "1", "975", "591", "599", "387", "267", "55", "246", "1", "673",
            "359", "226", "257", "855", "237", "1", "238", "1", "236", "235",
            "56", "86", "61", "57", "269", "242", "682", "506", "385", "53",
            "599", "357", "420", "225", "45", "253", "1", "1", "593", "20",
            "503", "240", "291", "372", "251", "500", "298", "679", "358",
            "33", "594", "689", "241", "220", "995", "49", "233", "350", "30",
            "299", "1", "590", "1", "502", "44", "224", "245", "592", "509",
            "504", "852", "36", "354", "91", "62", "98", "964", "353", "44",
            "972", "39", "1", "81", "44", "962", "7", "254", "686", "965",
            "996", "856", "371", "961", "266", "231", "218", "423", "370",
            "352", "853", "389", "261", "265", "60", "960", "223", "356",
            "692", "596", "222", "230", "262", "52", "691", "373", "377",
            "976", "382", "1", "212", "258", "95", "264", "674", "977", "31",
            "687", "64", "505", "227", "234", "683", "672", "850", "1", "47",
            "968", "92", "680", "970", "507", "675", "595", "51", "63", "48",
            "351", "1", "974", "262", "40", "7", "250", "590", "290", "1", "1",
            "590", "508", "1", "685", "378", "239", "966", "221", "381", "248",
            "232", "65", "1", "421", "386", "677", "252", "27", "82", "34",
            "94", "249", "597", "47", "268", "46", "41", "963", "886", "992",
            "255", "66", "243", "670", "228", "690", "676", "1", "216", "90",
            "993", "1", "688", "1", "256", "380", "971", "44", "1", "598",
            "998", "678", "379", "58", "84", "681", "212", "967", "260", "263"

    };

    public static final Map<String, String> countryCodeToCountryNameMap = new HashMap<>();
    public static final Map<String, String> countryNameToCountryCodeMap = new HashMap<>();

    static {
        for (int i = 0; i < countryNames.length && i < countryISOcodes.length; i++) {
            countryCodeToCountryNameMap.put(countryISOcodes[i], countryNames[i]);
            countryNameToCountryCodeMap.put(countryNames[i], countryISOcodes[i]);
        }
    }

//    public final static int[] countryFlags = {
//
//            R.drawable.flag_afghanistan, R.drawable.flag_albania,
//            R.drawable.flag_algeria, R.drawable.flag_american_samoa,
//            R.drawable.flag_andorra, R.drawable.flag_angola,
//            R.drawable.flag_anguilla, R.drawable.flag_antigua_and_barbuda,
//            R.drawable.flag_argentina, R.drawable.flag_armenia,
//            R.drawable.flag_aruba, R.drawable.flag_australia,
//            R.drawable.flag_austria, R.drawable.flag_azerbaijan,
//            R.drawable.flag_bahamas, R.drawable.flag_bahrain,
//            R.drawable.flag_bangladesh, R.drawable.flag_barbados,
//            R.drawable.flag_belarus, R.drawable.flag_belgium,
//            R.drawable.flag_belize, R.drawable.flag_benin,
//            R.drawable.flag_bermuda, R.drawable.flag_bhutan,
//            R.drawable.flag_bolivia,
//            R.drawable.flag_bonaire_sint_eustatius_and_saba,
//            R.drawable.flag_bosnia_and_herzegovina, R.drawable.flag_botswana,
//            R.drawable.flag_brazil,
//            R.drawable.flag_british_indian_ocean_territory,
//            R.drawable.flag_british_virgin_islands, R.drawable.flag_brunei,
//            R.drawable.flag_bulgaria, R.drawable.flag_burkina_faso,
//            R.drawable.flag_burundi, R.drawable.flag_cambodia,
//            R.drawable.flag_cameroon, R.drawable.flag_canada,
//            R.drawable.flag_cape_verde, R.drawable.flag_cayman_islands,
//            R.drawable.flag_central_african_republic, R.drawable.flag_chad,
//            R.drawable.flag_chile, R.drawable.flag_china,
//            R.drawable.flag_christmas_island, R.drawable.flag_colombia,
//            R.drawable.flag_comoros, R.drawable.flag_congo,
//            R.drawable.flag_cook_islands, R.drawable.flag_costa_rica,
//            R.drawable.flag_croatia, R.drawable.flag_cuba,
//            R.drawable.flag_curacao, R.drawable.flag_cyprus,
//            R.drawable.flag_czech_republic, R.drawable.flag_cote_divoire,
//            R.drawable.flag_denmark, R.drawable.flag_djibouti,
//            R.drawable.flag_dominica, R.drawable.flag_dominican_republic,
//            R.drawable.flag_ecuador, R.drawable.flag_egypt,
//            R.drawable.flag_el_salvador, R.drawable.flag_equatorial_guinea,
//            R.drawable.flag_eritrea, R.drawable.flag_estonia,
//            R.drawable.flag_ethiopia, R.drawable.flag_falkland_islands,
//            R.drawable.flag_faroe_islands, R.drawable.flag_fiji,
//            R.drawable.flag_finland, R.drawable.flag_france,
//            R.drawable.flag_french_guiana, R.drawable.flag_french_polynesia,
//            R.drawable.flag_gabon, R.drawable.flag_gambia,
//            R.drawable.flag_georgia, R.drawable.flag_germany,
//            R.drawable.flag_ghana, R.drawable.flag_gibraltar,
//            R.drawable.flag_greece, R.drawable.flag_greenland,
//            R.drawable.flag_grenada, R.drawable.flag_guadeloupe,
//            R.drawable.flag_guam, R.drawable.flag_guatemala,
//            R.drawable.flag_guernsey, R.drawable.flag_guinea,
//            R.drawable.flag_guineabissau, R.drawable.flag_guyana,
//            R.drawable.flag_haiti, R.drawable.flag_honduras,
//            R.drawable.flag_hong_kong, R.drawable.flag_hungary,
//            R.drawable.flag_iceland, R.drawable.flag_india,
//            R.drawable.flag_indonesia, R.drawable.flag_iran,
//            R.drawable.flag_iraq, R.drawable.flag_ireland,
//            R.drawable.flag_isle_of_man, R.drawable.flag_israel,
//            R.drawable.flag_italy, R.drawable.flag_jamaica,
//            R.drawable.flag_japan, R.drawable.flag_jersey,
//            R.drawable.flag_jordan, R.drawable.flag_kazakhstan,
//            R.drawable.flag_kenya, R.drawable.flag_kiribati,
//            R.drawable.flag_kuwait, R.drawable.flag_kyrgyzstan,
//            R.drawable.flag_laos, R.drawable.flag_latvia,
//            R.drawable.flag_lebanon, R.drawable.flag_lesotho,
//            R.drawable.flag_liberia, R.drawable.flag_libya,
//            R.drawable.flag_liechtenstein, R.drawable.flag_lithuania,
//            R.drawable.flag_luxembourg, R.drawable.flag_macao,
//            R.drawable.flag_macedonia, R.drawable.flag_madagascar,
//            R.drawable.flag_malawi, R.drawable.flag_malaysia,
//            R.drawable.flag_maldives, R.drawable.flag_mali,
//            R.drawable.flag_malta, R.drawable.flag_marshall_islands,
//            R.drawable.flag_martinique, R.drawable.flag_mauritania,
//            R.drawable.flag_mauritius, R.drawable.flag_mayotte,
//            R.drawable.flag_mexico, R.drawable.flag_micronesia,
//            R.drawable.flag_moldova, R.drawable.flag_monaco,
//            R.drawable.flag_mongolia, R.drawable.flag_montenegro,
//            R.drawable.flag_montserrat, R.drawable.flag_morocco,
//            R.drawable.flag_mozambique, R.drawable.flag_myanmar,
//            R.drawable.flag_namibia, R.drawable.flag_nauru,
//            R.drawable.flag_nepal, R.drawable.flag_netherlands,
//            R.drawable.flag_new_caledonia, R.drawable.flag_new_zealand,
//            R.drawable.flag_nicaragua, R.drawable.flag_niger,
//            R.drawable.flag_nigeria, R.drawable.flag_niue,
//            R.drawable.flag_norfolk_island, R.drawable.flag_north_korea,
//            R.drawable.flag_northern_mariana_islands, R.drawable.flag_norway,
//            R.drawable.flag_oman, R.drawable.flag_pakistan,
//            R.drawable.flag_palau, R.drawable.flag_palestine,
//            R.drawable.flag_panama, R.drawable.flag_papua_new_guinea,
//            R.drawable.flag_paraguay, R.drawable.flag_peru,
//            R.drawable.flag_philippines, R.drawable.flag_poland,
//            R.drawable.flag_portugal, R.drawable.flag_puerto_rico,
//            R.drawable.flag_qatar, R.drawable.flag_reunion,
//            R.drawable.flag_romania, R.drawable.flag_russia,
//            R.drawable.flag_rwanda, R.drawable.flag_saint_barthelemy,
//            R.drawable.flag_saint_helena,
//            R.drawable.flag_saint_kitts_and_nevis, R.drawable.flag_saint_lucia,
//            R.drawable.flag_saint_martin,
//            R.drawable.flag_saint_pierre_and_miquelon,
//            R.drawable.flag_saint_vincent_and_the_grenadines,
//            R.drawable.flag_samoa, R.drawable.flag_san_marino,
//            R.drawable.flag_sao_tome_and_principe,
//            R.drawable.flag_saudi_arabia, R.drawable.flag_senegal,
//            R.drawable.flag_serbia, R.drawable.flag_seychelles,
//            R.drawable.flag_sierra_leone, R.drawable.flag_singapore,
//            R.drawable.flag_sint_maarten_dutch_part, R.drawable.flag_slovakia,
//            R.drawable.flag_slovenia, R.drawable.flag_solomon_islands,
//            R.drawable.flag_somalia, R.drawable.flag_south_africa,
//            R.drawable.flag_south_korea, R.drawable.flag_spain,
//            R.drawable.flag_sri_lanka, R.drawable.flag_sudan,
//            R.drawable.flag_suriname, R.drawable.flag_svalbard_and_jan_mayen,
//            R.drawable.flag_swaziland, R.drawable.flag_sweden,
//            R.drawable.flag_switzerland, R.drawable.flag_syria,
//            R.drawable.flag_taiwan, R.drawable.flag_tajikistan,
//            R.drawable.flag_tanzania, R.drawable.flag_thailand,
//            R.drawable.flag_the_democratic_republic_of_congo,
//            R.drawable.flag_timorleste, R.drawable.flag_togo,
//            R.drawable.flag_tokelau, R.drawable.flag_tonga,
//            R.drawable.flag_trinidad_and_tobago, R.drawable.flag_tunisia,
//            R.drawable.flag_turkey, R.drawable.flag_turkmenistan,
//            R.drawable.flag_turks_and_caicos_islands, R.drawable.flag_tuvalu,
//            R.drawable.flag_us_virgin_islands, R.drawable.flag_uganda,
//            R.drawable.flag_ukraine, R.drawable.flag_united_arab_emirates,
//            R.drawable.flag_united_kingdom, R.drawable.flag_united_states,
//            R.drawable.flag_uruguay, R.drawable.flag_uzbekistan,
//            R.drawable.flag_vanuatu, R.drawable.flag_vatican,
//            R.drawable.flag_venezuela, R.drawable.flag_vietnam,
//            R.drawable.flag_wallis_and_futuna, R.drawable.flag_western_sahara,
//            R.drawable.flag_yemen, R.drawable.flag_zambia,
//            R.drawable.flag_zimbabwe };
//
//    public static int getCountryFlagFromCountryDialingCode(String dialingCode) {
//        int i = 0;
//        for (i = 0; i < countryDialingCodes.length; i++)
//            if (dialingCode.equals(countryDialingCodes[i]))
//                break;
//
//        if (i > 0 && i < countryFlags.length)
//            return countryFlags[i];
//        else
//            return 0;
//    }
//
//    public static int getIndexOfArrayFromCountryDialingCode(String dialingCode) {
//        int index = 0;
//        for (index = 0; index < countryDialingCodes.length; index++)
//            if (dialingCode.equals(countryDialingCodes[index]))
//                break;
//
//        return index;
//    }
//
//    public static int getIndexOfArrayFromCountryISOCode(String iso) {
//        int index = 0;
//        for (index = 0; index < countryISOcodes.length; index++)
//            if (iso.equalsIgnoreCase(countryISOcodes[index]))
//                return index;
//
//        return -1;
//    }

}