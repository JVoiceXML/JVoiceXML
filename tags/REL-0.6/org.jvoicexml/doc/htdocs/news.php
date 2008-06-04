<?php
header("Content-type: text/html; charset=iso-8859-1");
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"><!-- InstanceBegin template="/Templates/Template.dwt" codeOutsideHTMLIsLocked="false" -->
<head>
<!-- InstanceBeginEditable name="doctitle" -->
<title>JVoiceXML News</title>
<!-- InstanceEndEditable --> 
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<link href="favicon.ico" rel="shortcut icon" type="image/x-icon" />
<link href="style.css" rel="stylesheet" type="text/css" />
<!-- InstanceBeginEditable name="head" -->
<meta name="Description" content="" />
<meta name="Keywords" content="" />
<!-- InstanceEndEditable -->
</head>
<body bgcolor="#FFFFFF">
<table border="0" align="center" class="borderedcell">
  <tr>
    <td height="572" class="paddedcell"><table width="755" border="0" align="center" cellpadding="0" cellspacing="0">
        <tr> 
          <td colspan="3" bgcolor="#E7E7E0"><img src="images/logo.jpg" alt="jvoice XML logo" width="249" height="78" /> 
          </td>
        </tr>
        <tr> 
          <td colspan="3" bgcolor="#ECBA64"> <img src="images/spacer.gif" width="755" height="15" alt="" /></td>
        </tr>
        <tr> 
          <td width="224" height="27" bgcolor="#FFFFFF"> <img src="images/spacer.gif" width="224" height="27" alt="spacer" /></td>
          <td width="503" bgcolor="#E7E7E0"> <!-- InstanceBeginEditable name="EditRegion5" -->
            <table width="100%" border="0" cellpadding="0" cellspacing="0">
              <tr> 
                <td width="14%" height="27" bgcolor="#ECBA64" class="nav"><div align="center"><a href="index.php"><img src="images/bullet.gif" alt="b" width="6" height="8" border="0" /> 
                    <span class="headingsmallblack">HOME</span></a></div></td>
                <td width="26%" height="27" class="nav"><div align="center"><a href="documentation.htm"><img src="images/bullet.gif" alt="b" width="6" height="8" border="0" /> 
                    <span class="headingsmallblack"> DOCUMENTATION</span></a></div></td>
                <td width="21%" height="27" class="nav"><div align="center"><a href="downloads.htm"><img src="images/bullet.gif" alt="b" width="6" height="8" border="0" /> 
                    <span class="headingsmallblack">DOWNLOADS</span></a></div></td>
                <td width="24%" height="27" class="nav"><div align="center"><a href="wanttohelp.htm"><img src="images/bullet.gif" alt="b" width="6" height="8" border="0" /> 
                    <span class="headingsmallblack">WANT TO HELP?</span></a></div></td>
                <td width="15%" height="27" class="nav" ><div align="center"><a href="contact.htm"><img src="images/bullet.gif" alt="b" width="6" height="8" border="0" /> 
                    <span class="headingsmallblack">CONTACT</span></a></div></td>
              </tr>
            </table>
            <!-- InstanceEndEditable --></td>
          <td width="28" bgcolor="#FFFFFF"> <img src="images/spacer.gif" width="21" height="27" alt="spacer" /></td>
        </tr>
        <tr valign="top"> 
          <td height="454" colspan="3" bgcolor="#FFFFFF"> <p>&nbsp;</p>
            <!-- InstanceBeginEditable name="EditRegion3" -->
            <table width="100%" border="0">
              <tr> 
                <td height="420" valign="top" class="paddedcell"><p class="headinglarge">All 
                    News</p>
                  <p class="headinglarge"><img src="images/hr.gif" alt="horizontal line" width="400" height="7" /></p>
                  <p class="normaltext">&nbsp; </p>
                  <?php

	require_once("rss_fetch.inc");
	//$url = $_GET['url'];
	$url="http://sourceforge.net/export/rss2_projnews.php?group_id=128141&rss_fulltext=1";
	$rss = fetch_rss( $url );


	foreach ($rss->items as $item) {
			$href = $item['link'];
			$title = $item['title'];
			$description = $item['description'];
			$author = $item['author'];
			$date = $item['pubdate'];

			$array=explode("'",$description);
			$description=implode("\'",$array);

			echo "<p class=\"headingsmall\">$title\n</p>";
			echo "<p class=\"normaltext\">$description\n</p>";
			echo "<p class=\"normaltext\">&nbsp;\n</p>";
			echo "<p class=\"normaltext\">Author : $author \n</p>";
			echo "<p class=\"normaltext\">Date : $date \n</p>";
			echo "<br /><br /><br />";
	}


?>
                  <p class="headingsmall">&nbsp;</p>
                  <p class="normaltext">&nbsp;</p>
                  <p class="normaltext">&nbsp;</p>
                  </td>
              </tr>
            </table>
            <!-- InstanceEndEditable --></td>
        </tr>
        <tr> 
          <td height="12" colspan="3" bgcolor="#B8B8B7"> <img src="images/spacer.gif" width="755" height="12" alt="" /></td>
        </tr>
      </table>
      <table width="100%" border="0">
        <tr> <!-- InstanceBeginEditable name="EditRegion4" --> 
          <td><table width="100%" border="0" class="normaltext">
              <tr> 
                <td width="27%" height="39" valign="top"><a href="http://validator.w3.org/check?uri=referer"><img src="images/valid-xhtml10.png" alt="Valid XHTML 1.0 Transitional" height="31" width="88" border="0" /></a></td>
                <td width="44%"><p align="center" class="normaltext"><font size="-2">Licenced 
                    under GNU Lesser General Public License</font></p></td>
                <td width="29%" valign="top"><div align="right"><a href="http://sourceforge.net"><img
        src="http://sflogo.sourceforge.net/sflogo.php?group_id=128141&amp;type=1" width="88" height="31" border="0" alt="SourceForge.net Logo" /></a></div></td>
              </tr>
            </table></td>
          <!-- InstanceEndEditable --></tr>
      </table>
      <p align="center" class="normaltext">&nbsp;</p></td>
  </tr>
</table>
<p>&nbsp;</p></body>
<!-- InstanceEnd --></html>
