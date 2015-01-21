package com.knoll.cztools.kiwi.view;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.plaf.basic.BasicSliderUI.ActionScroller;

import com.knoll.cztools.cdl.parser.CDLParser;
import com.knoll.cztools.cdl.parser.ParseException;
import com.knoll.cztools.cdl.parser.TokenMgrError;
import com.knoll.cztools.kiwi.model.DS30Handler;
import com.knoll.cztools.kiwi.model.RuleRec;
import com.knoll.cztools.kiwi.model.StructureNodeRec;
import com.knoll.cztools.kiwi.model.Constants.Actions;
import com.knoll.cztools.kiwi.model.Constants.TabType;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

public class UIController implements Initializable {
	
	@FXML
	private Button srcFileButton;
	@FXML
	private Button destFolderButton;		
	@FXML
	private Label srcFileLabel;
	@FXML
	private Label destFolderLabel;
	@FXML
	private ChoiceBox<String> tabChoiceBox;
	@FXML
	private HBox hbox;
	@FXML
	private ToggleButton structureButton;
	@FXML
	private ToggleButton rulesButton;
	@FXML
	private TreeView treeView;
	@FXML
	private ChoiceBox<String> actionsChoiceBox;
	@FXML
	private Button actionsButton;
	@FXML
	private TextInputControl modelNameTextInput;
	@FXML
	private Accordion mainAccordion;
	@FXML
	private TitledPane hierarchyTitledPane;
	@FXML
	private TabPane detailsTabPane;
	@FXML
	private Tab structDetailsTab;
	@FXML
	private Tab ruleDetailsPane;
	@FXML
	private TextField ruleName;
	@FXML
	private TextArea ruleTextTextArea;
	@FXML
	private Button validateRuleTextBtn;
	
	private final ObservableList<String> eligibleActions = FXCollections.<String>observableArrayList();
	
	
	private ToggleGroup tabTypeTG;
	
	@FXML
	private TextArea logTextArea;
	
	private String mInitialDirectory = "C:\\Users\\VEERU\\Desktop\\Temp\\DS30Conversion\\demo";
	private DS30Handler mDS30Converter = DS30Handler.getInstance();
	private TabType mSelectedTabType = TabType.NONE;
	
	static Image FOLDER = new Image(UIController.class.getResourceAsStream("/media/images/tree_folder.gif"));
	static Image RULE_VALID_IMAGE = new Image(UIController.class.getResourceAsStream("/media/images/tree_freeformrule.gif"));
	static Image RULE_INVALID_IMAGE = new Image(UIController.class.getResourceAsStream("/media/images/tree_freeformrule_invalid.gif"));
	
	static Image STRUCT_OPTION_FEAT_IMAGE = new Image(UIController.class.getResourceAsStream("/media/images/tree_optionfeature.gif"));
	static Image STRUCT_OPTION_IMAGE = new Image(UIController.class.getResourceAsStream("/media/images/tree_option.gif"));
	static Image STRUCT_BOM_MODEL_IMAGE = new Image(UIController.class.getResourceAsStream("/media/images/tree_bommodel.gif"));
	static Image STRUCT_BOM_OC_ITEM_IMAGE = new Image(UIController.class.getResourceAsStream("/media/images/tree_bomoptionclass.gif"));
	static Image STRUCT_BOM_STD_ITEM_IMAGE = new Image(UIController.class.getResourceAsStream("/media/images/tree_bomstandarditem.gif"));
	
	
	private final CDLParser mParser = new CDLParser();
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		ContextMenu logTextCtxMenu = new ContextMenu();
		MenuItem clearMenuItem = new MenuItem("Clear");
		logTextCtxMenu.getItems().add(clearMenuItem);
		clearMenuItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				logTextArea.clear();
			}
		});
		logTextArea.setContextMenu(logTextCtxMenu);
		
		validateRuleTextBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				String ruleText = ruleTextTextArea.getText();
				RuleRecTreeItem ruleRecTreeItem = (RuleRecTreeItem)treeView.getSelectionModel().getSelectedItem();
				if(ruleText != null) {
					try {
						mParser.parseStatements(new StringReader(ruleText));
						ruleRecTreeItem.setGraphic(new ImageView(RULE_VALID_IMAGE));
						log("'" + ruleName.getText() + "' " + "rule is valid!");
					} catch (ParseException e) {
						log("'" + ruleName.getText() + "' " + "rule is invalid!");
						ruleRecTreeItem.setGraphic(new ImageView(RULE_INVALID_IMAGE));
						log(e.getMessage());
					} catch (TokenMgrError e) {
						log("'" + ruleName.getText() + "' " + "rule is invalid!");
						ruleRecTreeItem.setGraphic(new ImageView(RULE_INVALID_IMAGE));
						log(e.getMessage());
					}
				}
				
			}
		});
		
		
		mainAccordion.setExpandedPane(mainAccordion.getPanes().get(0));
		
		// Remove the tabs to start with
		detailsTabPane.getTabs().remove(structDetailsTab);
		detailsTabPane.getTabs().remove(ruleDetailsPane);
		
		
		treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem>() {

			@Override
			public void changed(ObservableValue<? extends TreeItem> observable,
					TreeItem oldValue, TreeItem newValue) {
				if(newValue != null) {
					Object treeItemValue = newValue.getValue();
					if(treeItemValue instanceof RuleRec) {
						RuleRec ruleRec = (RuleRec)treeItemValue;
						if(!ruleRec.isFolder()) {
							ruleName.setText(ruleRec.getName());
							ruleTextTextArea.setText(ruleRec.getText());
							ruleTextTextArea.setVisible(true);							
						}
						else {
							ruleName.setText(ruleRec.getName());
							ruleTextTextArea.setText(null);	
							ruleTextTextArea.setVisible(false);
						}

					}
					
//					ruleTextTextArea.setText(((RuleRec)newValue.getValue()).getText());
				}
				
			}
		});
		
		actionsButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				Actions selectedAction = Actions.getValueOf(actionsChoiceBox.getValue());
				
				try {
				switch(selectedAction) {
				case EXTRACT_STRUCTURE:
					mDS30Converter.extractStructure(modelNameTextInput.getText(), tabChoiceBox.getValue());
					break;
				case EXTRACT_RULES:
					mDS30Converter.extractRules(modelNameTextInput.getText(), tabChoiceBox.getValue());
					break;					
				}
				} catch(Exception ex) { 
					log(ex);
				}
				
			}
		});
		
		EventHandler<MouseEvent> mouseEvent = new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if(treeView.getRoot() != null) return;
				
				try {
					if(TabType.STRUCTURE.equals(mSelectedTabType)) {
						StructureNodeRec structNodeRec = mDS30Converter.buildStructureTree(modelNameTextInput.getText(),
                                                                                       tabChoiceBox.getValue());
						treeView.setRoot(new StructureNodeRecTreeItem(structNodeRec));
						treeView.getRoot().setExpanded(true);
					}
					else if(TabType.RULES.equals(mSelectedTabType)) {
						RuleRec ruleRec = mDS30Converter.buildRules(modelNameTextInput.getText(), tabChoiceBox.getValue());
						treeView.setRoot(new RuleRecTreeItem(ruleRec));
						treeView.getRoot().setExpanded(true);
					}					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		};
		hierarchyTitledPane.setOnMouseClicked(mouseEvent);
		
		tabChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observableValue,
					            String oldValue, 
					            String newValue) {
				mSelectedTabType = mDS30Converter.getTabType(newValue);
				mDS30Converter.setCurrentTab(newValue);
				
				resetSession();
				
				if(TabType.STRUCTURE.equals(mSelectedTabType) && tabTypeTG.getSelectedToggle() != structureButton) {
					
					detailsTabPane.getTabs().remove(ruleDetailsPane);
					detailsTabPane.getTabs().add(structDetailsTab);
					
					tabTypeTG.selectToggle(structureButton);
					eligibleActions.clear();
					eligibleActions.addAll(Actions.VALIDATE.label(),
							               Actions.EXTRACT_STRUCTURE.label(), 
							               Actions.EXTRACT_PROPERTIES.label(), 
							               Actions.EXTRACT_PROPERTY_VALUES.label());					
				}
				else if(TabType.RULES.equals(mSelectedTabType) && tabTypeTG.getSelectedToggle() != rulesButton) {
					
					detailsTabPane.getTabs().remove(structDetailsTab);
					detailsTabPane.getTabs().add(ruleDetailsPane);
					
					tabTypeTG.selectToggle(rulesButton);
					eligibleActions.clear();
					eligibleActions.addAll(Actions.VALIDATE.label(),
							               Actions.EXTRACT_RULES.label());						
				}
				else {
					tabTypeTG.selectToggle(null);

					detailsTabPane.getTabs().remove(structDetailsTab);
					detailsTabPane.getTabs().remove(ruleDetailsPane);					
				}
			}
		});
		
		actionsChoiceBox.setItems(eligibleActions);
		
		tabTypeTG = new ToggleGroup();
		structureButton.setToggleGroup(tabTypeTG);
		rulesButton.setToggleGroup(tabTypeTG);
		String pillButtonCss = getClass().getResource("/com/knoll/cztools/kiwi/view/Peach.css").toExternalForm();
		structureButton.getParent().getStylesheets().add(pillButtonCss);
		
		srcFileButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent evt) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setInitialDirectory(new File(mInitialDirectory));
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
						"Excel files (*.xlsx)", "*.xlsx");
				fileChooser.getExtensionFilters().add(extFilter);
				File file = fileChooser.showOpenDialog(null);
				
				if(file != null) {
//					mSourceFile = file;
					mInitialDirectory = file.getParent();
					srcFileLabel.setText(file.getPath());
					
					try {
						mDS30Converter.setSourceFile(file);
						tabChoiceBox.getItems().clear();
						tabChoiceBox.getItems().addAll(mDS30Converter.getSheets());
					} catch (Exception e) {
						log(e);
					}
				}
			}
		});
		
		
		destFolderButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent evt) {
				DirectoryChooser dirChooser = new DirectoryChooser();
				dirChooser.setInitialDirectory(new File(mInitialDirectory));
				File file = dirChooser.showDialog(null);
				
				if(file != null && mDS30Converter != null) {
					destFolderLabel.setText(file.getPath());
					mDS30Converter.setDestinationFolder(file);
				}
			}
		});
		
	}
	
	
	public void handleTabSelection(ActionEvent event) {
		System.out.println(event.getSource());
	}
	
	private void resetSession() {
		tabTypeTG.selectToggle(null);

		detailsTabPane.getTabs().remove(structDetailsTab);
		detailsTabPane.getTabs().remove(ruleDetailsPane);
		
		treeView.setRoot(null);
	}	
	
	private void log(Object msg) {
		if(msg instanceof Exception) {
//			StringWriter sw = new StringWriter();
//			((Exception)msg).printStackTrace(new PrintWriter(sw));
			msg = ((Exception)msg).getMessage();
		}
		
		
		logTextArea.appendText("\n" + msg.toString());
	}
}
