<?php
header("Content-type: text/html; charset=iso-8859-1");
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN"
"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
 <title>JVoiceXML - News</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<link rel="shortcut icon" href="jvoicexml_ico.ico" />
<link href="xjvoicexml.css" rel="stylesheet" type="text/css" />

<style type="text/css">

      table
      {
      width:880px;
      font-size : 12pt;
      color : #00003C;
      font-family: Helvetica, sans-serif;
      font-weight : bold;
      border:1px solid #00003C;
      padding:3px;
      margin-left:auto;
      margin-right:auto;
       margin-top:15px;

      }

      td,th
      {
      border:1px solid #00003C;
      }


      td.started{
      color : #FF5500;
      }

      td.full{
      color : #006600;
      }

      td.not{
      color : #990000;
      }
    </style>
</head>

<body>
<div id="placeholder">
<div id="banner">
        <div id="logo">
	<a href="http://jvoicexml.sourceforge.net/home.htm">
        <img src="banner.png" alt="JVoiceXML" width="700" height="30" /></a>
        </div>
        <div id="sflogo">
                <a href="http://sourceforge.net"><img src="http://sourceforge.net/sflogo.php?group_id=128141&amp;type=2" width="125" height="37" alt="SourceForge.net Logo" /></a>
        </div>
</div>

<div id="buttons">
      <a href="http://jvoicexml.sourceforge.net/home.htm"><object><div id="homeButton" class="buttonStyle">Home</div></object></a>
      <a href="http://jvoicexml.sourceforge.net/news.php"><object><div id="newsButton" class="buttonOverStyle">News</div></object></a>
      <a href="http://jvoicexml.sourceforge.net/documentation.htm"><object><div id="documentationButton" class="buttonStyle">Documentation</div></object></a>
      <a href="http://jvoicexml.sourceforge.net/downloads.htm"><object><div id="downloadsButton" class="buttonStyle">Downloads</div></object></a>
      <a href="http://jvoicexml.sourceforge.net/wanttohelp.htm"><object><div id="wanttohelpButton" class="buttonStyle">Want to help? </div></object></a>
      <a href="http://jvoicexml.sourceforge.net/contact.htm"><object><div id="contactButton" class="buttonStyle">Contact</div></object></a>
</div>


<div id="content">
       <span class="title01Style"> News </span>
        <br /><br />
<?php

	require_once("./magpierss/rss_fetch.inc");
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

			echo "<span class=\"title03Style\">$title</span><br /><br /> \n";
			echo "<span>$description</span><br /><br /> \n";
			echo "<span class=\"smlcontentStyle\">Author : </span><span class=\"smlcontentStyle\">$author</span><br /> \n";
			echo "<span class=\"smlcontentStyle\">Date : </span><span class=\"smlcontentStyle\">$date</span> \n";
			echo "<br /><br /><br /> \n";
	}


?>
</div>

<div id="W3C">
    <a href="http://validator.w3.org/check?uri=referer"><img
        src="http://www.w3.org/Icons/valid-xhtml11"
        alt="Valid XHTML 1.1" height="31" width="88" /></a>
 

    <a href="http://jigsaw.w3.org/css-validator/">
                  <img height="31" width="88" src="http://jigsaw.w3.org/css-validator/images/vcss" alt="Valid CSS!" />
         </a>
</div>
</div>

</body>
</html>
