package com.knoll.cztools.kiwi.view;

import com.knoll.cztools.kiwi.model.StructureNodeRec;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import static com.knoll.cztools.kiwi.view.UIController.*;

public class StructureNodeRecTreeItem extends TreeItem<StructureNodeRec> {
	
	private boolean isFirstTimeChildren = true;
	
	public StructureNodeRecTreeItem(StructureNodeRec structNodeRec) {
		super(structNodeRec);
		switch(getValue().getNodeType()) {
		case OPTION_FEATURE:
			setGraphic(new ImageView(STRUCT_OPTION_FEAT_IMAGE));
			break;
		case OPTION:
			setGraphic(new ImageView(STRUCT_OPTION_IMAGE));
			break;
		case ATO_MODEL:
		case PTO_MODEL:
			setGraphic(new ImageView(STRUCT_BOM_MODEL_IMAGE));
			break;		
		case ATO_OC:
		case PTO_OC:
			setGraphic(new ImageView(STRUCT_BOM_OC_ITEM_IMAGE));
			break;	
		case ATO_ITEM:
		case PURCHASED_ITEM:
			setGraphic(new ImageView(STRUCT_BOM_STD_ITEM_IMAGE));
			break;				
		}
	}

	@Override
	public ObservableList<TreeItem<StructureNodeRec>> getChildren() {
		// System.out.println(toString() + "getChildren()");
		if(isFirstTimeChildren) {
			isFirstTimeChildren = false;
			ObservableList<TreeItem<StructureNodeRec>> children = FXCollections.observableArrayList();
			for(StructureNodeRec childRec : getValue().getChildren()) {
				children.add(new StructureNodeRecTreeItem(childRec));
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
		return getValue().getName();
	}
	
}
