$(".bookEvent").click(function(e) {
  e.preventDefault();
  e.stopPropagation();
  var eid = $(e.target).attr("name");
  if (window.confirm("Etes-vous sûr de vouloir vous inscrire à cet événement ?")) {
    jsRoutes.controllers.Events.book(eid).ajax({
      success : function(data) {
        if (data == "notconnected") {
          console.log("okok");
          window.location.href = "/login?eid=" + eid;
        }
        console.log("success");
        window.location.reload()
        //console.log(data);
      },
      error: function(e) {
        console.log(e);
      }
    });
  }
});

$(".unBookEvent").click(function(e) {
  e.preventDefault();
  e.stopPropagation();
  var eid = $(e.target).attr("name");
  if (window.confirm("Etes-vous sûr de vouloir vous désinscrire de cet événement ?")) {
    jsRoutes.controllers.Events.unBook(eid).ajax({
      success : function(data) {
        window.location.reload();
        console.log("success");
      },
      error: function(e) {
        console.log(e);
      }
    });
  }
});

$(".deleteEvent").click(function(e) {
  e.preventDefault();
  e.stopPropagation();
  var eid = $(e.target).attr("data-eid");
  var uid = $(e.target).attr("data-uid");
  console.log(eid);
  console.log(uid);
  if (window.confirm("Etes-vous sûr de vouloir supprimer cet événement ?")) {
    jsRoutes.controllers.Events.deleteEvent(uid, eid).ajax({
      success : function(data) {
        window.location.reload();
        console.log("success");
      },
      error: function(e) {
        console.log(e);
      }
    });
  }
});