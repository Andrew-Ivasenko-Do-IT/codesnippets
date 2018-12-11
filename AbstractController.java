@RequestMapping(value = "/api")
public abstract class AbstractController<T extends AbstractModel> {
    final AbstractService<T> abstractService;
	
    AbstractController(AbstractService<T> abstractService) {
        this.abstractService = abstractService;
    }
	
    @GetMapping(value = "/{id}")
    public T get(@PathVariable String id) {
        return abstractService.getById(id);
    }

    @GetMapping
    public List<T> getAll(){
    	return abstractService.findAll();
    }
	
    @GetMapping(value = "/page")
    public Page<T> getAllPage(@RequestParam int page, @RequestParam int size){
        return abstractService.findAll(page, size);
    }

    @PostMapping
    public T save(@RequestBody T saved) {
        return abstractService.save(saved);
    }
	
    @PutMapping
    public T update(@RequestBody T updated) {
        return abstractService.save(updated);
    }
	
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        abstractService.delete(id);
    }
}