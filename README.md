#架构设计

整体框架分为两部分，一个是衍生品架构设计，另一个是模型架构设计
衍生品架构Base Class 是 Derivative， 然后sub class会是Option，Swap， Futures以及其他衍生品。Option class往下可以继续增加其他option，或者option组合，比如Snowball， Asian或者Bull call spread。 于此同时，PayoffScripts interface 可以方便不同的option 添加其独有的payoff， 以方便使用Monte Carlo Simulation。 

模型架构Base Class是Model， 然后sub class 是ClosedForm model 和 Monte Carlo Sim Model。 Greeks 的计算方式在这两个classes 里定义。因为并不是所有模型可以像BS model一样有相应的Greeks 公式。所以Greeks 计算方式是

![1](http://latex.codecogs.com/svg.latex?\frac{price_up-price_dn}{2*shockrate})

