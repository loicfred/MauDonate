package mau.donate.ai;

import mau.donate.objects.Association;
import mau.donate.objects.Campaign;
import org.springframework.ai.tool.annotation.Tool;

import java.util.ArrayList;
import java.util.List;

import static my.loic.utilities.db.spring.DatabaseService.dbService;
import static my.loic.utilities.util.Utilities.copyObject;

public class CampaignTools {

    @Tool(description = "Get a campaign information by name or keyword (both use same attribute)")
    public Object getCampaignInformation(String keyword) {
        List<Campaign> InCache = dbService.getAllWhere(Campaign.class, "Title LIKE ? OR Message LIKE ?", "%" + keyword + "%", "%" + keyword + "%");
        if (InCache.isEmpty()) return "There is no campaign with the name " + keyword + ".";
        Campaign c = new Campaign();
        copyObject(c, InCache);
        c.Image = null;
        return c;
    }

    @Tool(description = "Get a campaign's creator or association information by name or keyword (both use same attribute)")
    public Object getCampaignAssociation(String keyword) {
        List<Campaign> InCache = dbService.getAllWhere(Campaign.class, "Title LIKE ? OR Message LIKE ?", "%" + keyword + "%", "%" + keyword + "%");
        if (InCache.isEmpty()) return "There are no campaign information found " + keyword + ".";
        List<Association> c = new ArrayList<>();
        for (Campaign campaign : InCache) {
            Association c2 = new Association();
            copyObject(c2, campaign.getAssociation());
            c.add(c2);
        }
        return c;
    }
}
