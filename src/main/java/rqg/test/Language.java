package rqg.test;


/**
 * Created by rqg on 3/11/16.
 */
public class Language {
    private String key;
    private String chinese;
    private String english;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getChinese() {
        return chinese;
    }

    public void setChinese(String chinese) {
        this.chinese = chinese;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }


    public boolean check() {
        return !isEmpty(chinese) && !isEmpty(english);
    }

    @Override
    public String toString() {
        return "name = " + key + " c = " + chinese + " e = " + english;
    }

    /**
     * Returns true if the string is null or 0-length.
     *
     * @param str the string to be examined
     * @return true if str is null or zero length
     */
    public static boolean isEmpty(CharSequence str) {
        if (str == null || str.length() == 0)
            return true;
        else
            return false;
    }
}
