package ai.susi.server.api.cms;

import ai.susi.DAO;
import ai.susi.json.JsonObjectWithDefault;
import ai.susi.server.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * Created by chetankaushik on 06/06/17.
 * This Service deletes a expert as per given query.
 * http://localhost:4000/cms/deleteExpert.txt?model=general&group=knowledge&language=en&expert=whois
 */
public class DeleteExpertService extends AbstractAPIHandler implements APIHandler {

    private static final long serialVersionUID = -1755374387315534691L;

    @Override
    public BaseUserRole getMinimalBaseUserRole() {
        return BaseUserRole.ANONYMOUS;
    }

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
            json.put("deleted_file", ExpertName);

            //Add to git
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            Repository repository = null;
            try {
                repository = builder.setGitDir((DAO.susi_skill_repo))
                        .readEnvironment() // scan environment GIT_* variables
                        .findGitDir() // scan up the file system tree
                        .build();

                try (Git git = new Git(repository)) {
                    git.add()
                            .addFilepattern(expert_name)
                            .call();
                    // and then commit the changes
                    git.commit()
                            .setMessage("Deleted " + expert_name)
                            .call();

                } catch (GitAPIException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            json.put("Error", "Cannot find '" + expert + "' ('" + expert.getAbsolutePath() + "')");
        }

        return new ServiceResponse(json);

    }

}
