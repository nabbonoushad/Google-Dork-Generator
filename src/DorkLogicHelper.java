import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.Desktop;

import javax.swing.JOptionPane;

public class DorkLogicHelper {
    String desc = "";
    String required;
    StringBuilder fileType = new StringBuilder();
    String fileChoice;
    String website;
    String[] specifics;
    String[] preset;
    StringBuilder dork = new StringBuilder();
    DorkLogicHelper(String desc, String required, String fileChoice, String website, String[] specifics, String[] preset){
        this.desc = desc;
        this.required = required;
        this.fileChoice = fileChoice;
        this.website = website;
        this.specifics = specifics;
        this.preset = preset;
        
        this.fileChoice = fileChoice != null? fileChoice.trim() : null;
        if(this.fileChoice != null && this.fileChoice.equals("image")){
            if(specifics == null){
                this.fileType.append("filetype:png OR filetype:jpg OR filetype:webp OR filetype:gif");
            }else{
                for (int i = 0; i < this.specifics.length; i++) {
                    this.fileType.append("filetype:" + this.specifics[i].trim());
                    if(this.specifics.length-1 != i){
                        this.fileType.append(" OR ");
                    }
                }
            }
        }else if(this.fileChoice != null && this.fileChoice.equals("video")){
            this.fileType.append("filetype:mp4");
        }

        this.dork.append(desc + " ");
        for(String special : preset){
            this.dork.append(special+ " ");
        }
        if(!this.required.isEmpty()){
            this.required = this.required.replace("\"", "");
            if(this.required.contains(",")){
                String[] arr = this.required.split(",");
                for(String string : arr){
                    this.dork.append( '"' + string + '"' + " ");
                }
            }else{
                this.dork.append( '"' + this.required + '"' + " ");
            }
        }
        if(!this.fileType.toString().isEmpty()){
            this.dork.append(this.fileType);
        }
        if(this.website != null){
            this.website = this.website.replace("\"", "");
            if(this.website.contains(",")){
                int i = 0;
                String[] arr = this.website.split(",");
                for(String string : arr){
                    if(i<arr.length-1){
                        this.dork.append(" site:" + string + " |");
                    }else{
                        this.dork.append(" site:" + string);
                    }
                    i++;
                }
            }else{
                this.dork.append(" site:" + this.website);
            }
        }
        String encDork = dork.toString();
        try {
            encDork = URLEncoder.encode(encDork, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println(e);
        }
        String[] options = {"Copy", "Open In Google"};
        int choice = JOptionPane.showOptionDialog(null, "Dork: " + dork.toString(), "Complete!", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, 0);
        if(choice == 0){
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection stringSelection = new StringSelection(dork.toString());
            clipboard.setContents(stringSelection, null);
        }else{
            try {
                Desktop.getDesktop().browse(new URI("https://google.com/search?q=" + encDork));    
            } catch (Exception e) {
                System.out.println(e);    
            }
        }
    }
}