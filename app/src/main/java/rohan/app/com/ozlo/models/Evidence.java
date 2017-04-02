
package rohan.app.com.ozlo.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Evidence {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("choice_id")
    @Expose
    private String choiceId;
    @SerializedName("observed_at")
    @Expose
    private String observedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChoiceId() {
        return choiceId;
    }

    public void setChoiceId(String choiceId) {
        this.choiceId = choiceId;
    }

    public String getObservedAt() {
        return observedAt;
    }

    public void setObservedAt(String observedAt) {
        this.observedAt = observedAt;
    }

}
