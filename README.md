# SecurityManagerJarLoader
 Loads a jar file with the security manager

Example Policy File:

```json
{
 "mainArgs": [
  "test",
  "test1",
 ],
	"allowedFiles": [
		{"key": "{user.home}/runewild/-", "value": "read,write,delete,execute"},
		{"key": "{user.home}/runewild/", "value": "read,write,delete,execute"},
		{"key": "{user.home}/.androidid/", "value": "read,write,delete"},
		{"key": "{user.home}/.androidid/-", "value": "read,write,delete"},
		{"key": "{user.home}/jagex_cl_oldschool_version.dat", "value": "read,write,delete"},
		{"key": "C:/Users/Public", "value": "read,write,delete"},
		{"key": "C:/Users/Public/AppData", "value": "read,write,delete"},
		{"key": "C:/Users/Public/AppData/windll32.dat", "value": "read,write,delete"}
	],
	"socketPermissions": [
		{"key": "www.runewild.com", "value": "connect,resolve"},
		{"key": "download.runewild.com", "value": "connect,resolve"},
		{"key": "host141.runewild.com", "value": "connect,resolve"}
	],
	"urlPermissions": [
		{"key": "https://www.runewild.com/download/RuneWild.jar", "value": "*:*"}
	],
	"propertyPermissions": [
		{"key": "user.home", "value": "read"},
		{"key": "java.version", "value": "read"},
		{"key": "os.name", "value": "read"},
		{"key": "java.vendor", "value": "read"}
	],
	"runtimePermissions": [
		{"key": "preferences", "value": "read"},
		{"key": "exitVM", "value": "read"},
		{"key": "createClassLoader", "value": "read"}
	],
	"jarUrl": "https://www.runewild.com/download/RuneWild.jar"
}
```
