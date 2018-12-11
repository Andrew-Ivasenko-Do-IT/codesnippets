@RestController
@RequestMapping("/program-partners")
@Api(value = "/program-partners", description = "API for ProgramPartner entity")
public class ProgramPartnerController extends AbstractController<ProgramPartner> {

	private final ProgramPartnerService programPartnerService;
	private final ModelMapper modelMapper;

	ProgramPartnerController(AbstractService<ProgramPartner> abstractService) {
		super(abstractService);
		programPartnerService = (ProgramPartnerService) abstractService;
		modelMapper = new ModelMapper();
		modelMapper.addMappings(new PropertyMap<CountyPercentage, CountyPercentageDTO>() {
			protected void configure() {
				map().setCountyName(source.getCountyName());
			}
		});
	}

	@PostMapping("/assign-counties/{partnerId}")
	@ApiOperation("Provides implementation of assignment counties with percentages to partner")
	@ApiResponses({
			@ApiResponse(code = 400, message = "Returned code if any partner was found for current id"),
			@ApiResponse(code = 422, message = "Returned code if sum of percentages for counties is ge 1")
	})
	public ResponseEntity<?> assignCountiesToPartner(@PathVariable String partnerId,
	                                                 @RequestBody Set<CountyPercentage> countyPercentages) {
		try {
			ProgramPartner updatedPartner = programPartnerService.assignCountiesToPartner(partnerId, countyPercentages);
			if (updatedPartner == null) return ResponseEntity.badRequest().build();
			return ResponseEntity.ok(updatedPartner);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.unprocessableEntity().body(e.getMessage());
		}
	}

	@ApiOperation("Identifies program partner according to family's county and service")
	@ApiResponses({
			@ApiResponse(code = 400, message = "Returned if either county either service weren't found by their id"),
			@ApiResponse(code = 200, message = "Returned found partner or null if can't identify")
	})
	@GetMapping("/identify")
	public ResponseEntity<ProgramPartner> identifyProviderByCountyAndService(
			@RequestParam @ApiParam(value = "County id to identify") String county,
			@RequestParam @ApiParam(value = "Service id to identify") String service) {
		try {
			return ResponseEntity.ok(programPartnerService.identifyPartnerByCountyAndService(county, service));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@PutMapping("/some")
	@ApiOperation(value = "Update list of families")
	public List<ProgramPartnerElasticsearchDTO> updateAll(@RequestBody List<ProgramPartner> updated) {
		return abstractService.updateAll(updated)
				.stream()
				.map(source -> modelMapper.map(source, ProgramPartnerElasticsearchDTO.class))
				.collect(Collectors.toList());
	}
}
