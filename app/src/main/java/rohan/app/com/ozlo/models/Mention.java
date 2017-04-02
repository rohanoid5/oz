
package rohan.app.com.ozlo.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Mention {

    public Mention(String id, String orth, String choiceId, String name, String type) {
        this.id = id;
        this.orth = orth;
        this.choiceId = choiceId;
        this.name = name;
        this.type = type;
    }

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("orth")
    @Expose
    private String orth;
    @SerializedName("choice_id")
    @Expose
    private String choiceId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("type")
    @Expose
    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrth() {
        return orth;
    }

    public void setOrth(String orth) {
        this.orth = orth;
    }

    public String getChoiceId() {
        return choiceId;
    }

    public void setChoiceId(String choiceId) {
        this.choiceId = choiceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
