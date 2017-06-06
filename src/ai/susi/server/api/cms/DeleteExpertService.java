package ai.susi.server.api.cms;

import ai.susi.DAO;
import ai.susi.json.JsonObjectWithDefault;
import ai.susi.server.*;
import org.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * Created by chetankaushik on 06/06/17.
 * This Service deletes a expert as per given query.
 * http://localhost:4000/cms/deleteExpert.txt?model=general&group=knowledge&language=en&expert=whois
 */
public class DeleteExpertService  extends AbstractAPIHandler implements APIHandler {

    private static final long serialVersionUID = -1755374387315534691L;

    @Override
    public BaseUserRole getMinimalBaseUserRole() { return BaseUserRole.ANONYMOUS; }

    @Override
    public JSONObject getDefaultPermissions(BaseUserRole baseUserRole) {
        return null;
    }

    @Override
    public String getAPIPath() {
        return "/cms/deleteExpert.txt";
    }

    @Override
    public ServiceResponse serviceImpl(Query call, HttpServletResponse response, Authorization rights, final JsonObjectWithDefault permissions) {

        String model_name = call.get("model", "general");
        File model = new File(DAO.model_watch_dir, model_name);
        String group_name = call.get("group", "knowledge");
        File group = new File(model, group_name);
        String language_name = call.get("language", "en");
        File language = new File(group, language_name);
        String expert_name = call.get("expert", "whois");
        File expert = new File(language, expert_name + ".txt");
        String ExpertName = expert.getName();
        JSONObject json = new JSONObject(true);

        if (expert.exists()) {
            expert.delete();
            json.put("deleted_file",ExpertName);
        } else {
            json.put("Error","Cannot find '" + expert + "' ('" + expert.getAbsolutePath() + "')");
        }
        return new ServiceResponse(json);

    }
}
