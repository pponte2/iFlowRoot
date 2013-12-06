function sendFileToServer(formData,status)
{
  var $jQuery2 = jQuery.noConflict();
    
    var uploadURL = "http://localhost:8091/iFlow/Docs/upload?flowid=1&pid=1&subpid=1";
    alert("URL: "+uploadURL);
    //var uploadURL = "http://hayageek.com/examples/jquery/drag-drop-file-upload/upload.php"; //Upload URL
    //var extraData ={}; //Extra Data.
    var jqXHR=$jQuery2.ajax({
            xhr: function() {
            var xhrobj = $jQuery2.ajaxSettings.xhr();
            if (xhrobj.upload) {
                    xhrobj.upload.addEventListener('progress', function(event) {
                        var percent = 0;
                        var position = event.loaded || event.position;
                        var total = event.total;
                        if (event.lengthComputable) {
                            percent = Math.ceil(position / total * 100);
                        }
                        //Set progress
                        status.setProgress(percent);
                    }, false);
                }
            return xhrobj;
        },
    url: uploadURL,
    type: "POST",
    contentType:false,
    processData: false,
        cache: false,
        data: formData,
        success: function(data){
            status.setProgress(100);
 
            $jQuery2("#status1").append("File upload Done<br>");         
        }
    }); 
 
    status.setAbort(jqXHR);
}
 
var rowCount=0;
function createStatusbar(obj)
{
     var $jQuery2 = jQuery.noConflict();
     rowCount++;
     var row="odd";
     if(rowCount %2 ==0) row ="even";
     this.statusbar = $jQuery2("<div class='statusbar "+row+"'></div>");
     this.filename = $jQuery2("<div class='filename'></div>").appendTo(this.statusbar);
     this.size = $jQuery2("<div class='filesize'></div>").appendTo(this.statusbar);
     this.progressBar = $jQuery2("<div class='progressBar'><div></div></div>").appendTo(this.statusbar);
     this.abort = $jQuery2("<div class='abort'>Abort</div>").appendTo(this.statusbar);
     obj.after(this.statusbar);
 
    this.setFileNameSize = function(name,size)
    {
        var sizeStr="";
        var sizeKB = size/1024;
        if(parseInt(sizeKB) > 1024)
        {
            var sizeMB = sizeKB/1024;
            sizeStr = sizeMB.toFixed(2)+" MB";
        }
        else
        {
            sizeStr = sizeKB.toFixed(2)+" KB";
        }
 
        this.filename.html(name);
        this.size.html(sizeStr);
    };
    this.setProgress = function(progress)
    {       
        var progressBarWidth =progress*this.progressBar.width()/ 100;  
        this.progressBar.find('div').animate({ width: progressBarWidth }, 10).html(progress + "% ");
        if(parseInt(progress) >= 100)
        {
            this.abort.hide();
        }
    };
    this.setAbort = function(jqxhr)
    {
        var sb = this.statusbar;
        this.abort.click(function()
        {
            jqxhr.abort();
            sb.hide();
        });
    };
}
function handleFileUpload(files,obj)
{
   for (var i = 0; i < files.length; i++) 
   {
        var fd = new FormData();
        fd.append('file', files[i]);
        fd.append('flowid', 1);
        fd.append('pid', 2);
        fd.append('subpid', 3);
 
        var status = new createStatusbar(obj); //Using this we can set progress.
        status.setFileNameSize(files[i].name,files[i].size);
        //sendFileToServer(fd,status);
        new MultiUpload( $( 'dados' ).ficheiro1_add, 3, '_[{id}]', true, true );
   }
}
$(document).ready(function()
{
  var $jQuery2 = jQuery.noConflict();
  //alert("111");
var obj = $jQuery2("#dragandrophandler");
obj.on('dragenter', function (e) 
{
    var $jQuery2 = jQuery.noConflict();
    e.stopPropagation();
    e.preventDefault();
    $jQuery2(this).css('border', '2px solid #0B85A1');
});
obj.on('dragover', function (e) 
{
     e.stopPropagation();
     e.preventDefault();
});
obj.on('drop', function (e) 
{
  var $jQuery2 = jQuery.noConflict();
     $jQuery2(this).css('border', '2px dotted #0B85A1');
     e.preventDefault();
     var files = e.originalEvent.dataTransfer.files;
 
     //We need to send dropped files to Server
     handleFileUpload(files,obj);
});
$(document).on('dragenter', function (e) 
{
  //alert("222");
    e.stopPropagation();
    e.preventDefault();
});
$(document).on('dragover', function (e) 
{
  //alert("333");
  e.stopPropagation();
  e.preventDefault();
  obj.css('border', '2px dotted #0B85A1');
});
$(document).on('drop', function (e) 
{
  //alert("444");
    e.stopPropagation();
    e.preventDefault();
});
 
});