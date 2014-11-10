/**
 * File name : PageEntry.java
 *
 * This class represents the abstraction for a single URL entry.
 * Using Jsoup it fetches all the needed informations from the web page.
 *
 */

package fetcher.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fetcher.controller.MainController;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class PageEntry {
    String URL;
    String Name;
    String Description;
    Date DateAdded;
    String pageSnapshot;
    MainController controller;
    HashSet<String> tags;

    public PageEntry(String URLstr,MainController controller){
        this.URL = URLstr;
        this.controller = controller;
        this.pageSnapshot = "images/octopus.png";
        this.Description = "";
        this.tags = new HashSet<String>();
        //TODO: This is to 'reset' the filter by tags (Show all entries) . Probably needs a better solution.
        tags.add("all");
        Thread workerThread = new Thread(new Worker(true));
        workerThread.start();
    }

    /**
     * Constructor used just when loading from the json file.
     * @param controller
     */
    public PageEntry(MainController controller){
        this.controller = controller;
        this.tags = new HashSet<String>();
        tags.add("all");
        Thread workerThread = new Thread(new Worker(false));
        workerThread.start();
    }

    /**
     * Loads the page provided by the URL parameter and uses Jsoup to retrieve its content.
     */
    private void loadPage(){
        Document doc = null;
        try {
            doc = Jsoup.connect(URL).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.Name = doc.title();
        this.DateAdded = Calendar.getInstance().getTime();
        //Getting description
        if(doc.select("p").first() != null)
            Description = doc.select("p").first().text();
        //Getting the images , gets the first valid image (check isValidUrl function in utils class).
        Elements images = doc.select("img");
        //TODO: Make the image search way better, right now it just takes the first image regardless of size and significance.
        if(!images.isEmpty()){
            for(Element image : images){
                if(Utils.isValidURL(image.attr("src")) && Utils.isDirectLinkImage(image.attr("src"))){
                    pageSnapshot = image.attr("src");
                    break;
                }

            }
        }

    }

    /**
     * Creates a JsonObject containing a json representation of this PageEntry.
     * @return savedJson is the JsonObject containing the fields of this PageEntry.
     */
    public JsonObject saveEntry(){
        JsonObject savedJson = new JsonObject();
        JsonArray savedTags = new JsonArray();
        savedJson.addProperty("URL",URL);
        savedJson.addProperty("Name",Name);
        savedJson.addProperty("Description",Description);
        savedJson.addProperty("DateAdded", new SimpleDateFormat("dd MMMM yy , hh:mm:ss" , Locale.getDefault()).format(this.DateAdded));
        savedJson.addProperty("pageSnapshot",pageSnapshot);
        for(String tag : tags)
            savedTags.add(new JsonPrimitive(tag));
        savedJson.add("Tags",savedTags);
        return savedJson;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Date getDateAdded() {
        return DateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        DateAdded = dateAdded;
    }

    public String getPageSnapshot() {
        return pageSnapshot;
    }

    public void setPageSnapshot(String pageSnapshot) {
        this.pageSnapshot = pageSnapshot;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void addTag(String tag){
        this.tags.add(tag);
    }

    public HashSet<String> getTags() {
        return tags;
    }

    public void setTags(HashSet tags) {
        this.tags = tags;
    }

    class Worker implements Runnable{

        boolean loadFromURL;

        public Worker(boolean loadFromUrl){
            this.loadFromURL = loadFromUrl;
        }

        @Override
        public void run() {
            if(loadFromURL)loadPage();
            controller.notifyControllerNewEntry(PageEntry.this);
        }
    }
}

