import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DorkFrame extends JFrame implements ActionListener{
    String desc = "";
    String required;
    String fileChoice;
    String website;
    JLabel descLabel = new JLabel("Enter a small Description about what you want:* ");
    JTextField descText = new JTextField();
    JLabel requiredLabel = new JLabel("Enter a required string(anything you want specifically in the results, to add multiple specific words make it comma separate):* ");
    JTextField requiredText = new JTextField();
    JLabel filetypeLabel = new JLabel("Do you want a specific filetype?(default is set to all filetypes) ");
    JLabel websiteLabel = new JLabel("Do you want results from a specific website?(default is set to all websites) ");
    JLabel presetQuestionLabel = new JLabel("Do you want results for preset? ");
    JButton filetypeYesButton = new JButton("Yes");
    JButton websiteYesButton = new JButton("Yes");
    JButton addSpecificWords = new JButton("Yes");
    JButton createButton = new JButton("Create Dork");
    String specifics;
    String[] specificsArr;
    Map<String, String[]> wordsMap = new HashMap<>();
    ArrayList<String> userWants = new ArrayList<>();
    String userHomeDir = System.getProperty("user.home");
    File appData = new File(userHomeDir+"/AppData");
    File stay = new File(appData+"/Local/googleDorker/template");
    File file = new File(stay+"/wordDict.txt");
    JMenuBar menuBar = new JMenuBar();
    JMenu presetMenu = new JMenu("Preset");
    JMenuItem openLocation = new JMenuItem("Open Preset Location");
    JMenuItem editPreset = new JMenuItem("Edit Preset");
    DorkFrame(){
        openLocation.addActionListener(e -> openFolder());
        editPreset.addActionListener(e -> openPresetFile());
        presetMenu.add(openLocation);
        presetMenu.add(editPreset);
        menuBar.add(presetMenu);
        this.setSize(700, 600);
        this.setLayout(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setJMenuBar(menuBar);
        descLabel.setBounds(10, 0, 400, 20);
        this.add(descLabel);
        descText.setBounds(10, 20, 400, 50);
        this.add(descText);
        requiredLabel.setBounds(10, 70, 1000, 20);
        this.add(requiredLabel);
        requiredText.setBounds(10, 100, 400, 50);
        this.add(requiredText);
        filetypeLabel.setBounds(10, 160, 1000, 20);
        this.add(filetypeLabel);
        
        filetypeYesButton.setBounds(10, 190, 100, 50);
        filetypeYesButton.setFocusable(false);
        filetypeYesButton.addActionListener(this);
        this.add(filetypeYesButton);
        
        websiteLabel.setBounds(10, 240, 1000, 20);
        this.add(websiteLabel);
        presetQuestionLabel.setBounds(10, 320, 1000, 20);
        this.add(presetQuestionLabel);
        
        websiteYesButton.setBounds(10, 270, 100, 50);
        websiteYesButton.setFocusable(false);
        websiteYesButton.addActionListener(this);
        this.add(websiteYesButton);

        addSpecificWords.setBounds(10, 340, 100, 50);
        addSpecificWords.setFocusable(false);
        addSpecificWords.addActionListener(this);
        this.add(addSpecificWords);
        this.setLocationRelativeTo(null);
        if(!load()){
            System.out.println("Something went wrong");
        }

        createButton.setBounds(10, 410, 150, 100);
        createButton.setFocusable(false);
        createButton.addActionListener(this);
        this.add(createButton);
    }

    public void showImageInput(){
        String[] options = {"All Images", "Specific Image Extentions"};
        int choice = JOptionPane.showOptionDialog(null, "all or specific?", "ext's", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, 0);
        if(choice == 1){
            specifics = JOptionPane.showInputDialog("Enter Comma Separated File Extention(s): ");
            specificsArr = specifics.split(",");
        }
        fileChoice = "image";
    }
    public void showWordChoice(){
        if(!wordsMap.isEmpty()){
            ArrayList<String> options = new ArrayList<>();
            for(int i = 0; i < wordsMap.size(); i++){
                String name = wordsMap.keySet().toArray(new String[0])[i];
                options.add(name);
            }
            JList<String> list = new JList<>(options.toArray(new String[0]));
            JOptionPane.showInputDialog(null, list, "Choose Keywords", JOptionPane.QUESTION_MESSAGE);
            int[] selected = list.getSelectedIndices();
            for(int i : selected){
                String[] arr = wordsMap.get(options.get(i));
                for(String s : arr){
                    userWants.add(s);
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == filetypeYesButton){
            String[] options = {"Image", "Video"};//todo: make it support more file ext's
            int choice = JOptionPane.showOptionDialog(null, "Which Filetype?", "Filetype", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, 0);
            if(choice == 0){
                showImageInput();
            }else if(choice == 1){
                fileChoice = "video";
            }
        }
        else if(e.getSource() == websiteYesButton){
            website = JOptionPane.showInputDialog("Enter Website Domain:(e.g. example.com, comma separated if you want multiple): ");
        }
        else if(e.getSource() == addSpecificWords){
            showWordChoice();
        }
        else if(e.getSource() == createButton){
            desc = descText.getText();
            required = requiredText.getText();
            String[] userService = userWants.toArray(new String[0]);
            if(!desc.isEmpty()){
                new DorkLogicHelper(desc, required, fileChoice, website, specificsArr, userService);
            }else{
                descLabel.setForeground(Color.RED);
            }
        }
        else if(e.getSource()==editPreset){
            System.out.println("Edit Preset");
        }
    }
    public void openFolder(){
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (stay.exists() && stay.isDirectory()) {
                try {
                    desktop.open(stay);
                } catch (IOException er) {
                    System.err.println("Error opening folder: " + er.getMessage());
                }
            }
        } else {
            System.out.println("Desktop operations are not supported on this platform.");
        }
    }
    public void openPresetFile(){
        if (!file.exists()) {
            System.out.println("Error: File not found at " + file.getPath());
            return;
        }

        try {
            ProcessBuilder pb = new ProcessBuilder("notepad.exe", file.getPath());
            pb.start();
        } catch (IOException e) {
            System.err.println("Error opening Notepad: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public boolean load(){
        stay.mkdirs();
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (Exception e) {
                System.out.println("Cannot Create File");
            }
        }
        try {
            try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while((line = reader.readLine()) != null){
                    int stringSeprator = line.indexOf(":")+1;// plus 1 to skip the seprator
                    String name = line.substring(0, stringSeprator-1);//minus 1 to skip the seprator
                    String entries = line.substring(stringSeprator);
                    String[] arr = entries.split(",");
                    wordsMap.put(name, arr);
                }
                return true;
            } catch (Exception e) {
                System.out.println("Something went wrong");
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}