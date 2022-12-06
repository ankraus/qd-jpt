# quick and dirty jar patch tool
*caution: very experimental*  
## what does it do?
Create patches for jar files instead of distributing the whole application for every update

## usage
The following tools are in this repository:

### PatchCreator
This tool allows the user to choose two jar files and create a patch file which describes the differences and contains all relevant files.

### Patcher
Using this tool, the user can apply a previously created patch file to a jar. The resulting jar file should match the updated jar file that was provided at patch creation.

## known bugs
- there is no validation of inputs in the gui
- the patched jar does not always match the original jar in size on disk when compressed (even though the content seems to match)
- .target file in patch file is not currently used
- this application has not been tested