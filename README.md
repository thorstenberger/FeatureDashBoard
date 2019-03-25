# FeatureDashBoard
This eclipse plugin allows you to trace features located in a codebase to their location in the code with the help of in-file annotations and mapping files.

## Usage
In this section, explanation how to use the plugin is provided

### Views
Here the different views are explained

#### Feature List View
This is the main view of the plugin and it's from here you parse a specfic project. First you open the view by clicking *Window -> Show View -> Other -> Featuredashboard -> Feature List View*. In this view the different features that have been located in the project will be displayed. There are two actions in the toolbar: **Parse project** and **Sort table**. 
* Parse project

   To parse a project, you select a project in the *Project explorer* and then click this button. When this button has been clicked the project will be parsed in the background. By open the the   *Progress* view you can see if the parser is still active or by looking in the bottom-right corner. When the parser has finished, any features detected will be listed in this view.
   
* Sort table

   When the view has been filled with the features located in the project, you can click this action to sort the features in alphabetical order. 

By selecting one or multiple features, the *Feature-to-File View* and *Feature-to-Folder View* will be shown.

#### Feature-to-File view
Here the selected feature(s) will be visualized as a node. By double-clicking this node, all files belonging to this feature will be listed in a grid. Double-clicking a node will open an editor and highlighting the the code belonging to this feature. To remove the highlighting in files, there is a menu option called **Remove markers** in the Featuredashboard context menu (which is located in the main toolbar). Clicking on this will have different effects depending on what your selection in the *Project explorer* is. If your selection is a project then all highlights from all files in that project will be removed. If your selection is a folder then all highlights from all files in that folder will be removed. And if your selection is a file then all highlights from that file will be removed. 

By double-clicking in the view, outside of any filenode will bring you back to the feature overivew.

#### Feature-to-Folder view 
Here the selected feature(s) will be visualized where they are located in the folder structure. The folder structure will be that of a tree and only folders that contain any reference to the specific feature will be shown. This is visualized by a node connected to a foldernode that is colored green. There will only also be a connection between a *foldernode* and a *featurenode* if there are files located in that folder that belongs to a feature. 

#### Feature Metrics view 
This view will show you metrics about the features in the project. To open this view you click on: *Window -> Show View -> Other -> Featuredashboard -> Project Metrics View*. A table will be shown with different types of metrics. You can hover in the header to see information about that metric. It's also possible to click in the header of each column to sort the features by that value.

#### Project Metrics view
This view will allow you to compare different projects with eachother. To open this view you click on: *Window -> Show View -> Other -> Featuredashboard -> Feature Metrics View*. Here all parsed projects will be displayed and different metrics about then will be visible. The same interactions are possible as in the Feature Metrics view. Clicking on a project name in this view will update *Feature List View* and *Feature Metrics view* with the features for that project, which allows to quickly switching between different projects.  
   

### Builder
When a project is first parsed a **Builder** will be attached to the project. The purpose of this builder is to listen to modifications in the project. For instace, if you modify a file with annotations the other views will be automatically updated instead of having to re-parse the entire project.

You can either *remove* or *disable* the builder by doing the following: *Right-click project in the project explorer -> Properites -> Builders -> Uncheck or Remove*