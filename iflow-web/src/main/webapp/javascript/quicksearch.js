var $jQuery = jQuery.noConflict();
$jQuery(function(){
/* c�digo de init comentado, agora faz-se explicitamente em reloadBootstrapElements()  
  var j = 0;
$jQuery('.sortable').each(function(e){
    var tbId= "tb_"+j;
    $jQuery(this).attr('id', tbId);
    j++;
  var currTb = "#"+tbId;
  var inputId = "input_"+tbId;
  var inputTot = '<input type="text" placeholder="Search" autofocus="" name="search" value="" id="'+inputId+'" />'
  var qs = "table"+currTb+" tbody tr";
  var inputCal = "input#"+inputId;
  $jQuery(inputTot).insertBefore(currTb);
  $jQuery(inputCal).quicksearch(qs);
});
*/  
  
  
});
(function($, window, document, undefined) {
  $jQuery.fn.quicksearch = function (target, opt) {
    
    var timeout, cache, rowcache, jq_results, val = '', e = this, options = $.extend({ 
      delay: 100,
      selector: null,
      stripeRows: null,
      loader: null,
      noResults: '',
      bind: 'keyup',
      onBefore: function () { 
        return;
      },
      onAfter: function () { 
        return;
      },
      show: function () {
        this.style.display = "";
      },
      hide: function () {
        this.style.display = "none";
      },
      prepareQuery: function (val) {
        return val.toLowerCase().split(' ');
      },
      testQuery: function (query, txt, _row) {
        for (var i = 0; i < query.length; i += 1) {
          if (txt.indexOf(query[i]) === -1) {
            return false;
          }
        }
        return true;
      }
    }, opt);
    
    this.go = function () {
      
      var i = 0, 
      noresults = true, 
      query = options.prepareQuery(val),
      val_empty = (val.replace(' ', '').length === 0);
      
      for (var i = 0, len = rowcache.length; i < len; i++) {
        if (val_empty || options.testQuery(query, cache[i], rowcache[i])) {
          options.show.apply(rowcache[i]);
          noresults = false;
        } else {
          options.hide.apply(rowcache[i]);
        }
      }
      
      if (noresults) {
        this.results(false);
      } else {
        this.results(true);
        this.stripe();
      }
      
      this.loader(false);
      options.onAfter();
      
      return this;
    };
    
    this.stripe = function () {
      
      if (typeof options.stripeRows === "object" && options.stripeRows !== null)
      {
        var joined = options.stripeRows.join(' ');
        var stripeRows_length = options.stripeRows.length;
        
        jq_results.not(':hidden').each(function (i) {
          $(this).removeClass(joined).addClass(options.stripeRows[i % stripeRows_length]);
        });
      }
      
      return this;
    };
    
    this.strip_html = function (input) {
      var output = input.replace(new RegExp('<[^<]+\>', 'g'), "");
      output = $.trim(output.toLowerCase());
      return output;
    };
    
    this.results = function (bool) {
      if (typeof options.noResults === "string" && options.noResults !== "") {
        if (bool) {
          $(options.noResults).hide();
        } else {
          $(options.noResults).show();
        }
      }
      return this;
    };
    
    this.loader = function (bool) {
      if (typeof options.loader === "string" && options.loader !== "") {
         (bool) ? $(options.loader).show() : $(options.loader).hide();
      }
      return this;
    };
    
    this.cache = function () {
      
      jq_results = $(target);
      
      if (typeof options.noResults === "string" && options.noResults !== "") {
        jq_results = jq_results.not(options.noResults);
      }
      
      var t = (typeof options.selector === "string") ? jq_results.find(options.selector) : $(target).not(options.noResults);
      cache = t.map(function () {
        return e.strip_html(this.innerHTML);
      });
      
      rowcache = jq_results.map(function () {
        return this;
      });
      
      var e2 = rowcache;
      var e3 = [];
      var count = 0;
      var headers = 0;
      
      for (var key in e2) {
        if(key=="length")
          break;
        if((e2[key].firstElementChild.className != "table_sub_header")
          &&(e2[key].firstElementChild.className != "table_main_header")){
          //alert(e2[key].firstElementChild.className);
          e3[count] = e2[key];
          count++;
        }
        else {
          headers++;
        }
      }
  
      rowcache = e3;
      
      e2 = cache;
      e3 = [];
      count = 0;
      for (var key in e2) {
        if(key=="length")
          break;
        if(headers>0){
          headers--;
          
        }else
        {
          //alert(e2[key].firstElementChild.className);
          e3[count] = e2[key];
          count++;
        }
      }
  
      cache = e3;
      return this.go();
    };
    
    this.trigger = function () {
      this.loader(true);
      options.onBefore();
      
      window.clearTimeout(timeout);
      timeout = window.setTimeout(function () {
        e.go();
      }, options.delay);
      
      return this;
    };
    
    this.cache();
    this.results(true);
    this.stripe();
    this.loader(false);
    
    return this.each(function () {
      $(this).bind(options.bind, function () {
        val = $(this).val();
        e.trigger();
      });
    });
    
  };

}(jQuery, this, document));