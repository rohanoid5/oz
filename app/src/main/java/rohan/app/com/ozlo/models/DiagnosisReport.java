
package rohan.app.com.ozlo.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DiagnosisReport {

    @SerializedName("sex")
    @Expose
    private String sex;
    @SerializedName("age")
    @Expose
    private Integer age;
    @SerializedName("evidence")
    @Expose
    private List<Evidence> evidence = null;
    @SerializedName("extras")
    @Expose
    private Extras extras;
    @SerializedName("evaluated_at")
    @Expose
    private String evaluatedAt;

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public List<Evidence> getEvidence() {
        return evidence;
    }

    public void setEvidence(List<Evidence> evidence) {
        this.evidence = evidence;
    }

    public Extras getExtras() {
        return extras;
    }

    public void setExtras(Extras extras) {
        this.extras = extras;
    }

    public String getEvaluatedAt() {
        return evaluatedAt;
    }

    public void setEvaluatedAt(String evaluatedAt) {
        this.evaluatedAt = evaluatedAt;
    }

}
