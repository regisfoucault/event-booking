$(".bookEvent").click(function(e) {
  e.preventDefault();
  e.stopPropagation();
  var eid = $(e.target).attr("name");
  console.log("okok");
  console.log(eid);
  if (window.confirm("Etes-vous sûr de vouloir vous inscrire à cet événement ?")) {
    jsRoutes.controllers.Events.book(eid).ajax({
      success : function(data) { 
        //window.location.reload()
        if (data == "notconnected") {
          console.log("okok");
          window.location.href = "/login?eid=" + eid;
        }
        console.log("success");
        //console.log(data);
      },
      error: function(e) {
        console.log(e);
      }
    });
  }
});