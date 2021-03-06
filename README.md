# Rendering Graphviz graph in ASCII

## Example
This graph

![Work tab](https://raw.githubusercontent.com/nmeylan/graphviz-plain-to-ascii/master/doc/ex1.png)

Is rendered like this

```
                                                                                                             
                                      "CTCMY...SCEWX"                                                        
                                     ↙-----←↙ ↘→-↘                                                           
                                  ↙←↙             ↘→↘                                                        
                               ↙←↙                   ↘→↘                                                     
                              ↙                         ↘→↓                                                  
                            ↙     "EBLNX...DALUT"         "KZLM9...9APUJ"                                    
                           ↙            ↙ ↘                 ↙-←↙↓                                            
                         ←↙            ↙    ↘              ↙    |                                            
                          ↘           ↙      ↘           ↙      |                                            
                           ↘         ↙         ↓       ↙        |                                            
                            ↘       ↙     "ERNHS...DARXO"       |                                            
                             ↘      ↓         ↙←↙ ↘            ↙                                             
                              ↘     |        ↙      ↘       ↙←↙                                              
                               ↘   ↙     ↙←↙       ↙--↘---←↙                                                 
                                 ↓ ↓    ↙   ↙----←↙    ↓                                                     
                            "KXYWE...SGOSU"←        "9XDQC...NIGLS"                                          
                             ↙---←↙↓                      ↓↘→---↘                                            
                          ↙←↙      |                      |      ↘→↘                                         
                     ↙--←↙         |                      |         ↘→--↘                                    
                 ↓-←↙              ↓                      ↓              ↘→-↓                                
     "OWYFP...GMJPR"        "UVMYC...RGTPP"        "SBZZM...NDGLQ"          "NGWYY...BZEW9"                  
                                                                                                             

```

## Usage
Prerequisites:
```
<dependency>
   <groupId>guru.nidi</groupId>
   <artifactId>graphviz-java</artifactId>
</dependency>
```

```java
  File tmpFile = File.createTempFile("myGraph", ".txt");
  Graphviz.fromGraph(graph).render(Format.PLAIN_EXT).toFile(tmpFile);
  try (FileInputStream fis = new FileInputStream(tmpFile)) {
    tmpFile.deleteOnExit();
    AsciiRenderer asciiRenderer = new AsciiRenderer(fis, 10, 6);
      return asciiRenderer.render();
    } catch (IOException e) {
      e.printStackTrace();
    }
  return null;
```
