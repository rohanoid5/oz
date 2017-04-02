
package rohan.app.com.ozlo.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Parse {

    @SerializedName("mentions")
    @Expose
    private List<Mention> mentions = null;

    public List<Mention> getMentions() {
        return mentions;
    }

    public void setMentions(List<Mention> mentions) {
        this.mentions = mentions;
    }

}
