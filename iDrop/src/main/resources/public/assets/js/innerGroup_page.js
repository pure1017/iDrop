document.addEventListener('DOMContentLoaded', function () {
   var currentGroup = sessionStorage.getItem("currentGroup");
   var currentGroupBeginDate = sessionStorage.getItem("currentGroupBeginDate");
   var currentGroupDesc = sessionStorage.getItem("currentGroupDesc");
   var currentGroupCover = sessionStorage.getItem("currentGroupCover");
   document.getElementById("group_name_1").innerText = "My Group/" + currentGroup;
   document.getElementById("group_name_2").innerText = currentGroup;
   document.getElementById("group_img").src = currentGroupCover;
   document.getElementById("group_name").innerText = "Group Name: " + currentGroup;
   document.getElementById("begin_date").innerText = "Begin Date: " + currentGroupBeginDate;
   document.getElementById("group_desc").innerText = "Group Description: " + currentGroupDesc;
});