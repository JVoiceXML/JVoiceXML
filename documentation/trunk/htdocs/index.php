<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>JVoiceXML - The Open Source VoiceXML Interpreter</title>
<link href="style/global.css" rel="stylesheet" type="text/css" media="all" />
<link href="style/cms.css" rel="stylesheet" type="text/css" media="all" />
</head>
<body>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td valign="top"><table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td id="headerBg"><table width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td align="left" valign="middle">&nbsp;</td>
                <td width="860" height="139" align="left" valign="middle"><table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td height="91" id="jvoiceLogo"><a href="index.php"></a></td>
                    </tr>
                    <tr>
                      <td height="38" id="navigations"><ul>
                          <li><a href="index.php" id="active"><span class="red">►</span> HOME</a></li>
                          <li><a href="http://sourceforge.net/apps/mediawiki/jvoicexml/index.php?title=Main_Page"><span class="red">►</span> DOCUMENTATION</a></li>
                          <li><a href="downloads.html"><span class="red">►</span> DOWNLOADS</a></li>
                          <li><a href="reference.html"><span class="red">►</span> REFERENCES</a></li>
                          <li><a href="wanttohelp.html"><span class="red">►</span> WANT TO HELP?</a></li>
                          <li><a href="contact.html" class="last"><span class="red">►</span> CONTACT</a></li>
                        </ul></td>
                    </tr>
                    <tr>
                      <td height="10"></td>
                    </tr>
                  </table></td>
                <td align="left" valign="middle">&nbsp;</td>
              </tr>
            </table></td>
        </tr>
        <tr>
          <td valign="top"><table width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td class="leftShadow">&nbsp;</td>
                <td width="860" valign="top" id="mainContent"><table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td id="contents" valign="top"><h1>What is JVoiceXML?</h1>
                        <p>A free VoiceXML interpreter for JAVA with an open architecture for custom extensions. Demo implementation platforms are supporting JAVA APIs such as JSAPI and JTAPI.</p>
                        <p>JVoiceXML is an implementation of VoiceXML 2.1, the Voice Extensible Markup Language, specified at <a href="http://www.w3.org/TR/voicexml21/">http://www.w3.org/TR/voicexml21/</a>. This is an extension to VoiceXML 2.0, specified at <a href="http://www.w3.org/TR/voicexml20/">http://www.w3.org/TR/voicexml20/</a></p>
                        <p>VoiceXML is designed for creating audio dialogs that feature synthesized speech, digitized audio, recognition of spoken and DTMF key input, recording of spoken input, telephony, and mixed initiative conversations. Major goal is to have a platform independent implementation that can be used for free.</p>
                        <p>See also our project site at <a href="http://sourceforge.net/projects/jvoicexml">http://sourceforge.net/projects/jvoicexml</a></p>
                        <br />
                        <a href="http://sourceforge.net/donate/index.php?group_id=128141"><img src="images/supportProject.gif" align="bottom"/></a> &nbsp; &nbsp;&nbsp;<a href="#"><script type="text/javascript" src="http://www.ohloh.net/p/4208/widgets/project_thin_badge.js"></script></a>&nbsp; &nbsp; &nbsp;<script type="text/javascript" src="http://www.ohloh.net/p/4208/widgets/project_users_logo.js"></script> </td>
                      <td width="269" id="articleWrapper" valign="top"><h2>Latest News</h2>
                        <?php
						require_once("rss_fetch.inc");
					  	$url="http://sourceforge.net/export/rss2_projnews.php?group_id=128141&rss_fulltext=1";
						$rss = fetch_rss( $url );
						$num = 0;

						foreach ($rss->items as $item) {
						if ($num < 3)
						{
								$href = $item['link'];
								$title = $item['title'];
								$description = $item['description'];
								$author = $item['author'];
								$date = $item['pubdate'];
					
								$array=explode("'",$description);
								$description=implode("\'",$array);
									echo "<h3>$title</h3>";
								
								echo "<p>". substr($description,0,100) . "..." ."</p>";
								echo "<p><a href=\"news.php\"  class=\"readMore\">read more...</a></p>";
					
						$num = $num + 1;
						}
					}
                        ?>
                      </td>
                    </tr>
                  </table></td>
                <td class="rightShadow">&nbsp;</td>
              </tr>
            </table></td>
        </tr>
      </table></td>
  </tr>
  <tr>
    <td valign="bottom" id="footerArea"><table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td>&nbsp;</td>
          <td width="860"><table border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td class="footerLogo">&nbsp;</td>
                <td class="footerDivider licensedProduct" valign="middle" align="left" >Licenced under GNU Lesser General Public License<br />
                  <a href="http://www.webworldexperts.com">Web Design &amp; Web Development by Webworld Experts</a></td>
                <td class="footerDivider" align="center"><div id="imageWrapper"><a href="http://validator.w3.org/check?"><img src="images/w3schools.gif"  align="middle"/></a> <a href="http://sourceforge.net"> <img src="images/sourceforgenet.gif" align="middle" /></a></div></td>
              </tr>
            </table></td>
          <td>&nbsp;</td>
        </tr>
      </table></td>
  </tr>
</table>
</body>
</html>
