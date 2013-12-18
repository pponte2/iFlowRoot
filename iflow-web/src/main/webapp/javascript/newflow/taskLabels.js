var taskLabelsJSP = "TaskLabels/task_labels.jsp";
var idDivLabels = 'container_task_labels';

//Show Annotation From Task List
function showTaskLabels() {
  getJSP(taskLabelsJSP, idDivLabels);
}

function createLabel(labelid, editname, color) {
  var url = taskLabelsJSP + '?editfolder='+labelid+'&editname='+editname+'&color='+color;
  getJSP(url, idDivLabels);
}