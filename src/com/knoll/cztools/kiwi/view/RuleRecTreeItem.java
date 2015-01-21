package com.knoll.cztools.kiwi.view;

import com.knoll.cztools.kiwi.model.RuleRec;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import static com.knoll.cztools.kiwi.view.UIController.*;

public class RuleRecTreeItem extends TreeItem<RuleRec> {
	
	private boolean isFirstTimeChildren = true;
	
	public RuleRecTreeItem(RuleRec ruleRec) {
		super(ruleRec);
		ImageView image = ruleRec.isFolder() ? new ImageView(FOLDER) 
		                                     : ruleRec.isValid() ? new ImageView(RULE_VALID_IMAGE) : new ImageView(RULE_INVALID_IMAGE);
		setGraphic(image);
	}

	@Override
	public ObservableList<TreeItem<RuleRec>> getChildren() {
		if(isFirstTimeChildren) {
			isFirstTimeChildren = false;
			ObservableList<TreeItem<RuleRec>> children = FXCollections.observableArrayList();
			for(RuleRec childRec : getValue().getChildren()) {
				children.add(new RuleRecTreeItem(childRec));
			}
			super.getChildren().setAll(children);
		}
		return super.getChildren();
	}

	@Override
	public boolean isLeaf() {
		return getChildren().isEmpty();
	}

	@Override
	public String toString() {
		return getValue().toString();
	}
	
}
