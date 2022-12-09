package openproject.where42.api;

import java.util.ArrayList;
import java.util.Arrays;

public class Define {
    public static final String WHERE42_VERSION_PATH = "/v1";
    public static final String INTRA_VERSION_PATH = "/v2";
    public static final String SEOUL = "29";
    public static final ArrayList<String> WHERE42NAME = new ArrayList<>(Arrays.asList("sunghkim", "sojoo", "hyunjcho", "heeskim"));
    public static final ArrayList<String> WHERE42IMG = new ArrayList<>(Arrays.asList("img1", "img2", "무 이미지", "img4"));
    public static final ArrayList<String> WHERE42MSG = new ArrayList<>(Arrays.asList("msg1", "msg2", "너 내 동료가 돼라!!", "msg4"));
    public static final ArrayList<String> WHERE42SPOT = new ArrayList<>(Arrays.asList("spot1", "spot2", "제주도 푸른밤", "spot4"));
    public static final int OUT = 0;
    public static final int IN = 1;
    public static final int NONE = 2;
    public static final String PARSED = "parsed";
}
