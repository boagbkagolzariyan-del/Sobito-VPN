// Android helper: isolate Foxy's Go runtime from Psiphon and the VPN engine.
package main
import (
 "bufio"
 "encoding/json"
 "fmt"
 "net"
 "os"
 "path/filepath"
 "time"
 "github.com/armanlife/arman-vpn-client/core/internal/config"
 "github.com/armanlife/arman-vpn-client/core/internal/service"
 "github.com/armanlife/arman-vpn-client/core/mobile"
)
type setup struct { Dir string `json:"dir"`; User string `json:"user"`; Password string `json:"password"` }
func main() { if err:=run(); err!=nil { fmt.Fprintln(os.Stderr, err); os.Exit(1) } }
func run() error {
 scanner:=bufio.NewScanner(os.Stdin)
 if !scanner.Scan() { return fmt.Errorf("missing setup") }
 var s setup
 if err:=json.Unmarshal(scanner.Bytes(), &s); err!=nil { return err }
 if s.Dir=="" || s.User=="" || s.Password=="" { return fmt.Errorf("missing private setup") }
 paths,err:=service.ResolveDataPaths(s.Dir); if err!=nil{return err}
 cfg,_,err:=config.Load(paths.Config); if err!=nil{return err}
 l,err:=net.Listen("tcp4","127.0.0.1:0"); if err!=nil{return err}
 addr:=l.Addr().String(); l.Close()
 cfg.Proxy.Listen=addr; cfg.Proxy.Username=s.User; cfg.Proxy.Password=s.Password
 cfg.Routing.Enabled=false
 cfg.Network.UpstreamProxy=""; cfg.Network.UpstreamProxyUsername=""; cfg.Network.UpstreamProxyPassword=""
 if err=config.Save(paths.Config,cfg); err!=nil{return err}
 if err=mobile.Start(s.Dir,false); err!=nil{return err}; defer mobile.Stop()
 defer os.Remove(filepath.Join(s.Dir,"se7en-status.json"))
 quit:=make(chan struct{})
 go func(){defer close(quit); for scanner.Scan(){switch scanner.Text(){case "connect": mobile.Connect(); case "stop": return}}}()
 tick:=time.NewTicker(300*time.Millisecond); defer tick.Stop()
 for { select {
 case <-quit: return nil
 case <-tick.C:
  status:=map[string]interface{}{"running":mobile.Running(),"on":mobile.IsOn(),"pending":mobile.ConnectPending(),"proxy":mobile.ProxyAddress(),"api":mobile.DashboardAddress(),"exit":mobile.ExitCountry(),"error":mobile.LastError()}
  b,_:=json.Marshal(status); tmp:=filepath.Join(s.Dir,"se7en-status.tmp"); dest:=filepath.Join(s.Dir,"se7en-status.json")
  if err:=os.WriteFile(tmp,b,0600); err==nil { _=os.Rename(tmp,dest) }
  if !mobile.Running(){return fmt.Errorf("Foxy daemon stopped")}
 }}
}
