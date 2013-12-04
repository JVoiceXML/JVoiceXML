var webservice_url = "http://10.58.46.170:3012/sms_talk";

function get_sounds_for_number(number) {
    var ret = new Array();
		var i=0;
    var base_name = "sm://localsound::media_local::number::number_";
    for(i=0; i<number.length; i++) {
        ret.push(base_name + number[i]);
    }
    return ret;
}

function get_sounds_for_phone_number(number) {
    var ret = new Array();
		var i=0;
    var base_name = "sm://localsound::media_local::number::number_";
	if(number.length>0){
		ret.push(base_name + "0");
	}
    for(i=0; i<number.length; i++) {
        ret.push(base_name + number[i]);
    }
    return ret;
}

function get_sounds_for_age(number) {
    var ret = new Array();
    var base_name = "sm://localsound::media_local::number::number_";
    var ext_name = "sm://localsound::media_local::number::number_ext_";
    if (number <= 20) {
        ret.push(base_name + number);
        return ret;
    } 
    var mod = number[1];
    var div = number[0]*10; 
    ret.push(base_name + div);	
    if (mod == 1 || mod == 4) {
        ret.push(ext_name + mod);
    } else if (mod > 0) {
        ret.push(base_name + mod);
    } else {
    }
    return ret;
}

function get_receiver(calling_number) {		
    var ret = "";        
    var start = 1;
	var i=0;
    if (calling_number.charAt(1) == "0" && calling_number.length >= 10) {
        start = 2;
    } 
    for(i=start; i<calling_number.length; i++) {
        ret += calling_number.charAt(i);          
    }
    ("calling_number is "+ calling_number +"  start:" + start).print();
    return ret;
}

function get_sms_id(calling_number) {		
    var ret = "";
		var i=0;
    if (calling_number.length > 6) {//1354xxxx
        _log_debug("calling_number  = %s %s", calling_number, calling_number.length);
        return ret;
    }
    for(i=4; i<calling_number.length; i++) {//ret=smsID
        ret += calling_number.charAt(i);
        _log_debug("calling_number  = %s %s", calling_number, calling_number.length);
    } 
    return ret; 
}

function is_mobile_number(number) {    
    var pattern = /^(162|163|164|165|166|167|168|169|97|98|96)\d{7}$/g;
    var match = pattern.test(number);
    if (match) {
        return true; 
    } else {
        return false; 
    }
}
function _cdr_set_info(service_number, session_local_uri)
{
	com.vnxtele.util.VNXLog.info2("Variable value is: service_number:" + service_number);
	return ("service_number:" + service_number + " session_local_uri:"+session_local_uri);
}



